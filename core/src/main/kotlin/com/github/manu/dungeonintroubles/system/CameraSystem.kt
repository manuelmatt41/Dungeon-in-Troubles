package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.component.ImageComponent
import com.github.manu.dungeonintroubles.component.PlayerComponent
import com.github.manu.dungeonintroubles.event.MapChangeEvent
import com.github.quillraven.fleks.*
import ktx.log.logger
import ktx.tiled.height
import ktx.tiled.width

@AllOf([PlayerComponent::class, ImageComponent::class])
class CameraSystem(
    @Qualifier("gameStage") private val gameStage: Stage,
    private val imgCmps: ComponentMapper<ImageComponent>,
) : EventListener, IteratingSystem() {

    private var maxW = 0f
    private var maxH = 0f
    private val camera = gameStage.camera

    override fun onTickEntity(entity: Entity) {
        with(imgCmps[entity]) {
            val viewW = camera.viewportWidth * 0.5f

            camera.position.set(
                (image.x + 5f).coerceIn(viewW, maxW - viewW),
                maxH * 0.5f,
                camera.position.z
            )
        }
    }

    override fun handle(event: Event?): Boolean {
        return when (event) {
            is MapChangeEvent -> {
                maxW = event.map.width.toFloat()
                maxH = event.map.height.toFloat()
                true
            }

            else -> false
        }
    }

    companion object {
        private val log = logger<CameraSystem>()
    }
}