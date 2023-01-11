package com.github.manu.dungeonintroubles.system

import com.github.manu.dungeonintroubles.component.MoveComponent
import com.github.manu.dungeonintroubles.component.PhysicComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2

@AllOf([MoveComponent::class, PhysicComponent::class])
class MoveSystem(
    private val moveCmps: ComponentMapper<MoveComponent>,
    private val physicsCmps: ComponentMapper<PhysicComponent>,
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        val physcmp = physicsCmps[entity]
        val moveCmp = moveCmps[entity]
        val mass = physcmp.body.mass
        val (velX, velY) = physcmp.body.linearVelocity


        physcmp.impulse.set(
            mass * (moveCmp.speedX - velX),
            mass * (moveCmp.speedY * moveCmp.sin - velY)
        )
    }

    companion object {
        private val log = logger<MoveSystem>()
    }
}