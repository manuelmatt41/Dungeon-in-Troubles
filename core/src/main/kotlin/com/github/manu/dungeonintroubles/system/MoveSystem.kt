package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.component.*
import com.github.manu.dungeonintroubles.event.MoveEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.quillraven.fleks.*
import ktx.log.logger

@AllOf([MoveComponent::class, PhysicComponent::class])
class MoveSystem(
    @Qualifier("uiStage") private val uiStage: Stage,
    private val moveCmps: ComponentMapper<MoveComponent>,
    private val physicsCmps: ComponentMapper<PhysicComponent>,
    private val playerCmps: ComponentMapper<PlayerComponent>,
    private val npcsCmps: ComponentMapper<NpcComponent>,
    private val imgCmps: ComponentMapper<ImageComponent>,
) : IteratingSystem() {

    private var flagToIncreaseSpeed: Float = 750f

    override fun onTickEntity(entity: Entity) {
        val physcmp = physicsCmps[entity]
        val moveCmp = moveCmps[entity]
        val mass = physcmp.body.mass
        val velX = physcmp.body.linearVelocity.x

        physcmp.impulse.x = mass * (moveCmp.speed * moveCmp.cos - velX);

        when (entity) {
            in playerCmps -> {
                with((playerCmps[entity])) {
                    meter += (moveCmp.speed * deltaTime) * 4f
                    uiStage.fire(MoveEvent(AnimationModel.PLAYER, meter))

                    if (meter < LIMIT_SPEED && meter >= flagToIncreaseSpeed) {
                        moveCmp.speed += 1f
                        actualSpeed = moveCmp.speed
                        flagToIncreaseSpeed += 750f
                        log.debug { "${moveCmp.speed}" }
                    }
                }
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
        private const val LIMIT_SPEED = 5250f
    }
}