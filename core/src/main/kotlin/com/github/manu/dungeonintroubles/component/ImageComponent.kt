package com.github.manu.dungeonintroubles.component

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.github.manu.dungeonintroubles.actor.FlipImage
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Qualifier

class ImageComponent {
    lateinit var image: FlipImage

    companion object {
        class ImageComponentListener(
            @Qualifier("gameStage") private val gameStage: Stage,
        ) : ComponentListener<ImageComponent> {
            override fun onComponentAdded(entity: Entity, component: ImageComponent) {
                gameStage.addActor(component.image)
            }

            override fun onComponentRemoved(entity: Entity, component: ImageComponent) {
                gameStage.root.removeActor(component.image)
            }

        }
    }
}