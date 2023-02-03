package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.math.MathUtils
import com.github.manu.dungeonintroubles.component.*
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
    private val npcsCmps: ComponentMapper<NpcComponent>,
    private val imgCmps: ComponentMapper<ImageComponent>,
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        val physcmp = physicsCmps[entity]
        val moveCmp = moveCmps[entity]
        val mass = physcmp.body.mass
        val (velX, velY) = physcmp.body.linearVelocity

        if (moveCmp.root) {
            return
        }

        physcmp.impulse.x = mass * (moveCmp.speed * moveCmp.cos - velX);

        when (entity) {
            in playerCmps -> {

            playerCmps[entity].meter += (moveCmp.speed * deltaTime) * 4f
            }

            in npcsCmps -> {
                with(npcsCmps[entity]) {
                    if (timeChangeDirection > 0f) {
                        timeChangeDirection -= deltaTime
                        return@with
                    }

                    moveCmp.cos = if (MathUtils.random(0, 1) == 0) -1f else 1f
                    timeChangeDirection = 2f
                }
            }
         }

        imgCmps.getOrNull(entity)?.let { imageCmp ->
            if (moveCmp.cos != 0f) {
                imageCmp.image.flipX = moveCmp.cos < 0
            }
        }
    }

    companion object {
        private val log = logger<MoveSystem>()
    }
}