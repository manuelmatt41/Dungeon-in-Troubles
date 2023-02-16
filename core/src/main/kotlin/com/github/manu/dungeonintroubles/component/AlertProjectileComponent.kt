package com.github.manu.dungeonintroubles.component

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Qualifier
import ktx.actors.plusAssign
import ktx.math.vec2

class AlertProjectileComponent {
    var position: Vector2 = vec2()
    var time: Float = 0f
    var lifeSpan: Float = 5f
    lateinit var label: Label

    companion object {
        class AlertProjectileComponentListener(
            @Qualifier("uiStage") private val uiStage: Stage,
        ) : ComponentListener<AlertProjectileComponent> {
            override fun onComponentAdded(entity: Entity, component: AlertProjectileComponent) {
                uiStage.addActor(component.label)
                // how the floating text is dissapear of the screen
//                component.label += Actions.fadeIn(1f, Interpolation.fade)
                component.label += fadeOut(3f, Interpolation.fade)
            }

            override fun onComponentRemoved(entity: Entity, component: AlertProjectileComponent) {
                uiStage.root.removeActor(component.label)
            }
        }
    }
}