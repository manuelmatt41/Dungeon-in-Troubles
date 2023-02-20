package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.github.manu.dungeonintroubles.event.*
import com.github.quillraven.fleks.IntervalSystem
import ktx.assets.disposeSafely
import ktx.log.logger

/**
 * Sistema que se encarga de la musica y los sonidos del juego
 * @property prefs Datos guardados que contiene las canciones
 *
 */
class AudioSystem(
    private val prefs: Preferences
) : EventListener, IntervalSystem() {

    /**
     * Mapa que guarda las canciones cargadas para ahorrar recursos
     */
    private val musicCache = mutableMapOf<String, Music>()

    /**
     * Mapa que guarda los sonidos cargadas para ahorrar recursos
     */
    private val soundCache = mutableMapOf<String, Sound>()

    /**
     * Lista de espera de sonidos para reproducir de forma ordenada por la entrada del sonido
     */
    private val soundRequest = mutableMapOf<String, Sound>()

    /**
     * Por cada vez que se ejecuta el sistema repoduce los sonidos que estan en cola y pone una cancion de fondo si no hay ninguna iniciada
     */
    override fun onTick() {
        if (musicCache.isEmpty()) {
            playnNewMusic(if (!prefs.contains("song")) "1.ogg" else prefs.getString("song"))
        }

        if (soundRequest.isEmpty()) {
            // no sound to play -> do nothing
            return
        }

        soundRequest.values.forEach { if (prefs.contains("sound")) prefs.getInteger("sound").toFloat() / 100f else 1f }
        soundRequest.clear()
    }

    /**
     * Se ejecuta cuando se lanza un evento y mira si el e evento lanzado lo coge y ejecuta una parte de codigo
     *
     * @param event Evento que se ha lanzado
     *
     * @return Devuelve true si se coge el evento sino devuelve false
     */
    override fun handle(event: Event): Boolean {
        return when (event) {
            is GetCoinSoundEvent -> {
                queueSound("audio/sounds/${event.model.atlasKey}.ogg")
                true
            }

            is CrossPortalSoundEvent -> {
                queueSound("audio/sounds/portal.ogg")
                true
            }

            is DeadSoundEvent -> {
                queueSound("audio/sounds/death.ogg")
                true
            }

            is SpawnProjectilesSoundEvent -> {
                queueSound("audio/sounds/fireball.ogg")
                true
            }

            is ChangeSettingsEvent -> {
                //Guarda los cambios de los ajustes
                log.debug { "Cambio config" }
                musicCache.forEach { it.value.volume = prefs.getInteger("music") / 100f }
                playnNewMusic(prefs.getString("song"))
                true
            }

            else -> false
        }
    }

    /**
     * Carga y guarda una cancion en el cache y la repoduce
     *
     * @param songName Nombre de la cancion que se va a cargar
     */
    private fun playnNewMusic(songName: String) {
        val music = musicCache.getOrPut(songName) {
            Gdx.audio.newMusic(
                Gdx.files.internal(
                    "audio/music/$songName"
                )
            ).apply {
                isLooping = true
                volume =if (prefs.contains("music")) prefs.getInteger("music").toFloat() / 100f else 1f
            }
        }


        musicCache.forEach {
            if (it.value.isPlaying) {
                it.value.stop()
            }
        }

        music.play()
    }

    /**
     * Carga y guarda en cache los sonidos y los pone el cola de espera
     *
     * @param soundPath Nombre del sonido que se quiere cargar y reproducir
     */
    private fun queueSound(soundPath: String) {
        if (soundPath in soundRequest) {
            // already queued -> do nothing
            return
        }

        val sound = soundCache.getOrPut(soundPath) {
            log.debug { "Sound $soundPath created" }
            Gdx.audio.newSound(Gdx.files.internal(soundPath))
        }

        soundRequest[soundPath] = sound
    }

    /**
     * Al cerrar el mundo de entidades libera los recursos
     */
    override fun onDispose() {
        musicCache.values.forEach { it.disposeSafely() }
        soundCache.values.forEach { it.disposeSafely() }
    }

    companion object {
        private val log = logger<AudioSystem>()
    }
}