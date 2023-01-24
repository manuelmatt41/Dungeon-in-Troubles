package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.DungeonInTroubles.Companion.UNIT_SCALE
import com.github.manu.dungeonintroubles.component.*
import com.github.manu.dungeonintroubles.event.GetCointEvent
import com.github.manu.dungeonintroubles.event.SpawnObjectsEvent
import com.github.manu.dungeonintroubles.event.TrapCollisionEvent
import com.github.manu.dungeonintroubles.extension.entity
import com.github.manu.dungeonintroubles.extension.fire
import com.github.quillraven.fleks.*
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2
import ktx.math.vec2
import ktx.tiled.width

@AllOf([PhysicComponent::class, ImageComponent::class])
@NoneOf([DespawnComponent::class])
class PhysicSystem(
    private val physicWorld: World,
    @Qualifier("gameStage") private val gameStage: Stage,
    private val imgCmps: ComponentMapper<ImageComponent>,
    private val physicsCmps: ComponentMapper<PhysicComponent>,
    private val playerCmps: ComponentMapper<PlayerComponent>,
    private val trapCmps: ComponentMapper<TrapComponent>,
    private val coinCmps: ComponentMapper<CoinComponent>,
    private val despawnCmps: ComponentMapper<DespawnComponent>,
    private val spawnPointCmps: ComponentMapper<SpawnPointComponent>,
) : ContactListener, IteratingSystem(interval = Fixed(1 / 60f)) {

    private var trapOrCoin: Boolean = true
    private var map: TiledMap? = null;

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
        physicCmp.body.applyLinearImpulse(physicCmp.impulse, physicCmp.body.worldCenter, true)
        physicCmp.impulse.setZero()
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

        val collisionAWithTrap = entityA in playerCmps && entityB in trapCmps
        val collisionBWithTrap = entityB in playerCmps && entityA in trapCmps

        val collisionAWithCoin = entityA in playerCmps && entityB in coinCmps
        val collisionBWithCoin = entityB in playerCmps && entityA in coinCmps

        val collisionAWithSpawnPoint = entityA in playerCmps && entityB in spawnPointCmps
        val collisionBWithSpawnPoint = entityB in playerCmps && entityA in spawnPointCmps

        when {
            collisionAWithTrap -> {
                gameStage.fire(TrapCollisionEvent(AnimationModel.TRAP))

                configureEntity(entityA) {
                    despawnCmps.add(it)
                }
                Gdx.input.vibrate(100)
            }

            collisionBWithTrap -> {
                gameStage.fire(TrapCollisionEvent(AnimationModel.TRAP))

                configureEntity(entityB) {
                    despawnCmps.add(it)
                }
                Gdx.input.vibrate(100)
            }

            collisionAWithCoin -> {
                with(playerCmps[entityA]) {
                    coins++;
                }

                configureEntity(entityB) {
                    despawnCmps.add(it)
                }

                gameStage.fire(GetCointEvent(AnimationModel.COIN))
            }

            collisionBWithCoin -> {
                with(playerCmps[entityB]) {
                    coins++;
                }

                configureEntity(entityA) {
                    despawnCmps.add(it)
                }

                gameStage.fire(GetCointEvent(AnimationModel.COIN))
            }

            collisionAWithSpawnPoint -> {
                map = TmxMapLoader().load(
                    Gdx.files.internal(if (trapOrCoin) "map/traps.tmx" else "map/coin_map.tmx").path()
                )

                gameStage.fire(
                    SpawnObjectsEvent(
                        if (trapOrCoin) "trap_zone_${
                            MathUtils.random(
                                1,
                                map!!.layers.count
                            )
                        }" else "coin_zone_${MathUtils.random(1, map!!.layers.count)}",
                        map!!,
                        vec2((imgCmps[entityB].image.x + map!!.width * 0.5f) / UNIT_SCALE)
                    )
                )

                trapOrCoin = !trapOrCoin

                configureEntity(entityB) {
                    spawnPointCmps.remove(it)
                }
            }

            collisionBWithSpawnPoint -> {
                map = TmxMapLoader().load(
                    Gdx.files.internal(if (trapOrCoin) "map/traps.tmx" else "map/coin_map.tmx").path()
                )

                gameStage.fire(
                    SpawnObjectsEvent(
                        if (trapOrCoin) "trap_zone_${
                            MathUtils.random(
                                1,
                                map!!.layers.count
                            )
                        }" else "coin_zone_${MathUtils.random(1, map!!.layers.count)}",
                        map!!,
                        vec2((imgCmps[entityB].image.x + map!!.width * 0.5f) / UNIT_SCALE)
                    )
                )

                trapOrCoin = !trapOrCoin

                configureEntity(entityA) {
                    spawnPointCmps.remove(it)
                }
            }
        }
    }

    override fun endContact(contact: Contact) {
        val entityA = contact.fixtureA.entity
        val entityB = contact.fixtureB.entity

        val collisionAWithCoin = entityA in playerCmps && entityB in coinCmps
        val collisionBWithCoin = entityB in playerCmps && entityA in coinCmps

        log.debug { "End contact" }
        when {
            collisionAWithCoin -> {
                gameStage.fire(GetCointEvent(AnimationModel.COIN))
            }

            collisionBWithCoin -> {
                gameStage.fire(GetCointEvent(AnimationModel.COIN))
            }
        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {

    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {

    }

    companion object {
        private val log = logger<PhysicSystem>()
    }
}
