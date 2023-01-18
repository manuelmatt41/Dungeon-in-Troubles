package com.github.manu.rpg.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.github.manu.dungeonintroubles.event.MapChangeEvent
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

        if (musicCache.all { !it.value.isPlaying }) {

        }
    }

    override fun handle(event: Event): Boolean {
        when (event) {
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
                        it.value.stop()
                    }
                    music.play()
                }
                return true
            }
        }

        return false
    }

    private fun queueSound(soundPath: String) {
        log.debug { "Queue sound $soundPath" }

        if (soundPath in soundRequest) {
            // already queued -> do nothing
            return
        }

        val sound = soundCache.getOrPut(soundPath) {
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