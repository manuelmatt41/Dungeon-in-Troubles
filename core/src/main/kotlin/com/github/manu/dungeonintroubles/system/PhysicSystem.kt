package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.World
import com.github.manu.dungeonintroubles.component.*
import com.github.manu.dungeonintroubles.extension.entity
import com.github.manu.dungeonintroubles.extension.isDinamicBody
import com.github.manu.dungeonintroubles.extension.isStaticBody
import com.github.quillraven.fleks.*
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2
import ktx.math.vec2

@AllOf([PhysicComponent::class, ImageComponent::class])
class PhysicSystem(
    private val physicWorld: World,
    private val imgCmps: ComponentMapper<ImageComponent>,
    private val physicsCmps: ComponentMapper<PhysicComponent>,
    private val tilesCmps: ComponentMapper<TiledComponent>,
    private val collisionCmps: ComponentMapper<CollisionComponent>,
    private val moveCmps: ComponentMapper<MoveComponent>,
) : ContactListener, IteratingSystem(interval = Fixed(1 / 60f)) {

    init {
        physicWorld.setContactListener(this)
    }

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
//            physicCmp.body.applyLinearImpulse(physicCmp.impulse, physicCmp.body.worldCenter, true)
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


    override fun beginContact(contact: Contact) {
        val entityA = contact.fixtureA.entity
        val entityB = contact.fixtureB.entity

        val isEntityATiledCollisionSensor = entityA in tilesCmps && contact.fixtureA.isSensor
        val isEntityBCollisionFixture = entityB in collisionCmps && !contact.fixtureB.isSensor

        val isEntityACollisionFixture = entityA in collisionCmps && !contact.fixtureA.isSensor
        val isEntityBTiledCollisionSensor = entityB in tilesCmps && contact.fixtureB.isSensor

//        val isEntityAAiSensor = entityA in aiCmps && contact.fixtureA.isSensor && contact.fixtureA.userData == AI_SENSOR
//        val isEntityBAiSensor = entityB in aiCmps && contact.fixtureB.isSensor && contact.fixtureB.userData == AI_SENSOR

        when {
            isEntityATiledCollisionSensor && isEntityBCollisionFixture -> {
                tilesCmps[entityA].nearbyEntities += entityB
                moveCmps[entityB].speedY = 0f;
            }

            isEntityBTiledCollisionSensor && isEntityACollisionFixture -> {
                tilesCmps[entityB].nearbyEntities += entityA
            }

            entityA in moveCmps && isEntityBCollisionFixture -> {
                moveCmps[entityA].speedY = 0f
                log.debug { "Entity $entityA" }
            }

            entityB in moveCmps && isEntityACollisionFixture -> {
                moveCmps[entityB].speedY = 0f
                log.debug { "Entity $entityB" }
            }
//            isEntityAAiSensor && isEntityBCollisionFixture -> {
//                aiCmps[entityA].nearbyEntities += entityB
//            }
//
//            isEntityBAiSensor && isEntityACollisionFixture -> {
//                aiCmps[entityB].nearbyEntities += entityA
//            }
        }
    }

    override fun endContact(contact: Contact) {
        val entityA = contact.fixtureA.entity
        val entityB = contact.fixtureB.entity
        val isEntityATiledCollisionSensor = entityA in tilesCmps && contact.fixtureA.isSensor
        val isEntityBTiledCollisionSensor = entityB in tilesCmps && contact.fixtureB.isSensor

//        val isEntityAAiSensor = entityA in aiCmps && contact.fixtureA.isSensor && contact.fixtureA.userData == AI_SENSOR
//        val isEntityBAiSensor = entityB in aiCmps && contact.fixtureB.isSensor && contact.fixtureB.userData == AI_SENSOR

        when {
            isEntityATiledCollisionSensor && !contact.fixtureB.isSensor -> {
                tilesCmps[entityA].nearbyEntities -= entityB
            }

            isEntityBTiledCollisionSensor && !contact.fixtureA.isSensor -> {
                tilesCmps[entityB].nearbyEntities -= entityA
            }

            entityA in moveCmps -> {
                moveCmps[entityA].speedY = DEFAULT_SPEED_Y
            }

            entityB in moveCmps -> {
                moveCmps[entityB].speedY = DEFAULT_SPEED_Y
            }
//            isEntityAAiSensor && !contact.fixtureB.isSensor -> {
//                aiCmps[entityA].nearbyEntities -= entityB
//            }
//
//            isEntityBAiSensor && !contact.fixtureA.isSensor -> {
//                aiCmps[entityB].nearbyEntities -= entityA
//            }
        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
        contact.isEnabled =
            (contact.fixtureA.isStaticBody() && contact.fixtureB.isDinamicBody()) ||
                    (contact.fixtureB.isStaticBody() && contact.fixtureA.isDinamicBody())
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {

    }

    companion object {
        private val log = logger<PhysicSystem>()
    }
}
