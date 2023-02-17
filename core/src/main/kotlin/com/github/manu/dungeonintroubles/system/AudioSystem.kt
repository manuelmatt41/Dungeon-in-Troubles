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

class AudioSystem(
    private val prefs: Preferences
) : EventListener, IntervalSystem() {

    private val musicCache = mutableMapOf<String, Music>()
    private val soundCache = mutableMapOf<String, Sound>()
    private val soundRequest = mutableMapOf<String, Sound>()

    override fun onTick() {
        if (musicCache.isEmpty()) {
            playnNewMusic(if (!prefs.contains("song")) "1.ogg" else prefs.getString("song"))
        }

        if (soundRequest.isEmpty()) {
            // no sound to play -> do nothing
            return
        }

        soundRequest.values.forEach { it.play(prefs.getInteger("sound") / 100f) }
        soundRequest.clear()
    }

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
                log.debug { "Cambio config" }
                musicCache.forEach { it.value.volume = prefs.getInteger("music") / 100f }
                playnNewMusic(prefs.getString("song"))
                true
            }

            else -> false
        }
    }

    private fun playnNewMusic(songName: String) {
        val music = musicCache.getOrPut(songName) {
            Gdx.audio.newMusic(
                Gdx.files.internal(
                    "audio/music/$songName"
                )
            ).apply {
                isLooping = true
                volume = prefs.getInteger("music") / 100f
            }
        }


        musicCache.forEach {
            if (it.value.isPlaying) {
                it.value.stop()
            }
        }

        music.play()
    }

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

    override fun onDispose() {
        musicCache.values.forEach { it.disposeSafely() }
        soundCache.values.forEach { it.disposeSafely() }
    }

    companion object {
        private val log = logger<AudioSystem>()
    }
}