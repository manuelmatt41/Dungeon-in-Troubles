package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.github.manu.dungeonintroubles.event.*
import com.github.quillraven.fleks.IntervalSystem
import ktx.assets.disposeSafely
import ktx.log.logger
import ktx.tiled.propertyOrNull

class AudioSystem : EventListener, IntervalSystem() {

    private val musicCache = mutableMapOf<String, Music>()
    private val soundCache = mutableMapOf<String, Sound>()
    private val soundRequest = mutableMapOf<String, Sound>()

    override fun onTick() {
        if (soundRequest.isEmpty()) {
            // no sound to play -> do nothing
            return
        }

        soundRequest.values.forEach { it.play(1f) }
        soundRequest.clear()
    }

    override fun handle(event: Event): Boolean {
        return when (event) {
            is MapChangeEvent -> {
                event.map.propertyOrNull<String>("music")?.let { path ->
                    log.debug { "Changing music to $path" }
                    val music = musicCache.getOrPut(path) {
                        Gdx.audio.newMusic(Gdx.files.internal(path)).apply {
                            isLooping = true
                            volume = 0.1f
                        }
                    }
                    musicCache.forEach {
                        if (it.value.isPlaying) {
                            return@let
                        }
                    }
                    music.play()
                }
                true
            }

            is GetCoinSoundEvent -> {
                queueSound("audio/sounds/${event.model.atlasKey}.wav")
                true
            }

            is TrapSoundCollisionEvent -> {
                queueSound("audio/sounds/${event.model.atlasKey}.wav")
                true
            }

            is CrossPortalSoundEvent -> {

                queueSound("audio/sounds/portal.ogg")
                true
            }

            is DeadSoundEvent -> {
                queueSound("audio/sounds/death.wav")
                true
            }

            is SpawnProjectilesEvent -> {
                queueSound("audio/sounds/fireball.wav")
                true
            }

            else -> false
        }
    }


    private fun queueSound(soundPath: String) {
//        log.debug { "Queue sound $soundPath" }

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