package com.github.manu.dungeonintroubles.system

import com.github.manu.dungeonintroubles.component.MoveComponent
import com.github.manu.dungeonintroubles.component.PhysicComponent
import com.github.manu.dungeonintroubles.component.PlayerComponent
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
    private val playerCmps: ComponentMapper<PlayerComponent>,
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        val physcmp = physicsCmps[entity]
        val moveCmp = moveCmps[entity]
        val mass = physcmp.body.mass
        val velX = physcmp.body.linearVelocity.x

        physcmp.impulse.x = mass * (moveCmp.speed * moveCmp.cos - velX);

        if (entity in playerCmps) {
            playerCmps[entity].meter += (moveCmp.speed * deltaTime) * 4f
//            log.debug { String.format("Meters: %.3f", playerCmps[entity].meter) }
        }
    }

    companion object {
        private val log = logger<MoveSystem>()
    }
}