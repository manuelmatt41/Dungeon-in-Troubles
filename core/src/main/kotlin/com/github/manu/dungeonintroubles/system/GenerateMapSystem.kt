package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.component.*
import com.github.manu.dungeonintroubles.event.CrossPortalSoundEvent
import com.github.manu.dungeonintroubles.event.MapChangeEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.quillraven.fleks.*
import ktx.box2d.body
import ktx.box2d.chain
import ktx.log.logger
import ktx.math.vec2
import ktx.tiled.height
import ktx.tiled.width

@AllOf([PlayerComponent::class])
class GenerateMapSystem(
    @Qualifier("gameStage") private val gameStage: Stage,
    private val physicWorld: World,
    private val imgCmps: ComponentMapper<ImageComponent>,
    private val despaswnCmps: ComponentMapper<DespawnComponent>,
    private val playerCmps: ComponentMapper<PlayerComponent>,
) : EventListener, IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        with(imgCmps[entity]) {
            if (entity in playerCmps) {

                if (image.x > 250f) { //TODO Reaction with a collision not with coordinate in physic system
                    gameStage.fire(CrossPortalSoundEvent())

                    changeMap(entity)
                }
            }
        }
    }

    private fun changeMap(playerEntity: Entity? = null) {
        world.removeAll()

        val nextMap =
            TmxMapLoader().load(Gdx.files.internal("map/${MathUtils.random(1, NUMBER_OF_MAPS)}.tmx").path())

        var playerCmp = if (playerEntity == null) PlayerComponent() else  playerCmps[playerEntity]
        gameStage.fire(MapChangeEvent(nextMap,playerCmp))
    }

    override fun handle(event: Event?): Boolean {
        return when (event) {
            is MapChangeEvent -> {
                // world boundary chain shape to restrict movement within tiled map
                world.entity {
                    val width = event.map.width.toFloat()
                    val height = event.map.height.toFloat()

                    add<PhysicComponent> {
                        body = physicWorld.body(BodyDef.BodyType.StaticBody) {
                            position.set(0f, 0f)
                            fixedRotation = true
                            allowSleep = false

                            // collision with top of the map without friction
                            chain(
                                vec2(0f, height),
                                vec2(width, height)
                            ) {
                                friction = 0f
                            }

                            // collision with de ground without friction
                            chain(
                                vec2(0f, 1.5f),
                                vec2(width, 1.5f)
                            ) {
                                friction = 0f
                            }
                        }
                    }
                }
                true
            }

            else -> false
        }
    }

    companion object {
        private val log = logger<GenerateMapSystem>()
        const val NUMBER_OF_MAPS = 3
        const val LAYER_POSIBILITIES = 3
    }
}