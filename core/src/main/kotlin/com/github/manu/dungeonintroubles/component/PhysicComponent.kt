package com.github.manu.dungeonintroubles.component

import com.badlogic.gdx.physics.box2d.Body
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import ktx.math.vec2

class PhysicComponent {
    val prevPosition = vec2()
    val impulse = vec2()
    val offset = vec2()
    val size = vec2()

    lateinit var body: Body

    companion object {
        class PhysicComponentListener : ComponentListener<PhysicComponent> {
            override fun onComponentAdded(entity: Entity, component: PhysicComponent) {
                component.body.userData = entity
            }

            override fun onComponentRemoved(entity: Entity, component: PhysicComponent) {
                val body = component.body

                body.world.destroyBody(body)
                body.userData = null
            }

        }
    }
}