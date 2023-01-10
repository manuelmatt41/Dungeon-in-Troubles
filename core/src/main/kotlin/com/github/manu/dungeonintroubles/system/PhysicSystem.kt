package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.World
import com.github.manu.dungeonintroubles.component.ImageComponent
import com.github.manu.dungeonintroubles.component.PhysicComponent
import com.github.quillraven.fleks.*
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2

@AllOf([PhysicComponent::class, ImageComponent::class])
class PhysicSystem(
    private val physicWorld: World,
    private val imgCmps: ComponentMapper<ImageComponent>,
    private val physicsCmps: ComponentMapper<PhysicComponent>,
) : IteratingSystem(interval = Fixed(1 / 60f)) {

    override fun onUpdate() {
        if (physicWorld.autoClearForces) {
            log.debug { "AutoClearForces must be set to false to guarantee a correct physic simulation." }
            physicWorld.autoClearForces = false
        }

        super.onUpdate()
        physicWorld.clearForces()
    }

    override fun onTick() {
        super.onTick()
        physicWorld.step(deltaTime, 6, 2)
    }

    override fun onTickEntity(entity: Entity) {
        val physicCmp = physicsCmps[entity]

        physicCmp.prevPosition.set(physicCmp.body.position)

        if (!physicCmp.impulse.isZero) {
            physicCmp.body.applyLinearImpulse(physicCmp.impulse, physicCmp.body.worldCenter, true)
            physicCmp.impulse.setZero()
        }
    }

    override fun onAlphaEntity(entity: Entity, alpha: Float) {
        val physicCmp = physicsCmps[entity]
        val imgCmp = imgCmps[entity]

        val (prevX, prevY) = physicCmp.prevPosition
        val (bodyX, bodyY) = physicCmp.body.position

        imgCmp.image.run {
            setPosition(
                MathUtils.lerp(prevX, bodyX, alpha) - width * 0.5f,
                MathUtils.lerp(prevY, bodyY, alpha) - height * 0.5f
            )
        }
    }

    companion object {
        private val log = logger<PhysicSystem>()
    }
}