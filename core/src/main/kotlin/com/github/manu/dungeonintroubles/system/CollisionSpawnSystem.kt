package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.*
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.github.manu.dungeonintroubles.component.CollisionComponent
import com.github.manu.dungeonintroubles.component.PhysicComponent
import com.github.manu.dungeonintroubles.component.TiledComponent
import com.github.manu.dungeonintroubles.event.CollisionDespawnEvent
import com.github.manu.dungeonintroubles.event.MapChangeEvent
import com.github.manu.dungeonintroubles.extension.forEachCell
import com.github.manu.dungeonintroubles.extension.physicCmpFromShape2D
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.box2d.body
import ktx.box2d.chain
import ktx.collections.GdxArray
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2
import ktx.math.vec2
import ktx.tiled.height
import ktx.tiled.isEmpty
import ktx.tiled.shape
import ktx.tiled.width

@AllOf([PhysicComponent::class, CollisionComponent::class])
class CollisionSpawnSystem(
    private val physicWorld: World,
    private val physicCmps: ComponentMapper<PhysicComponent>,
) : EventListener, IteratingSystem() {

    private val tiledLayer = GdxArray<TiledMapTileLayer>()
    private val processedCells = mutableSetOf<Cell>()

    override fun onTickEntity(entity: Entity) {
        val (entityX, entityY) = physicCmps[entity].body.position

        tiledLayer.forEach { layer ->
            layer.forEachCell(entityX.toInt(), entityY.toInt(), SPAWN_AREA_SIZE) { tileCell, x, y ->
                if (tileCell.tile.objects.isEmpty()) {
                    //cell is not linked to a collision object -> do nothing
                    return@forEachCell
                }

                if (tileCell in processedCells) {
                    // tileCell already processed -> do nothing
                    return@forEachCell
                }

                processedCells.add(tileCell)

                tileCell.tile.objects.forEach { mapObject ->
                    world.entity {
                        physicCmpFromShape2D(physicWorld, x, y, mapObject.shape)
                        add<TiledComponent> {
                            this.cell = tileCell
                            nearbyEntities.add(entity)
                        }
                    }
                }
            }
        }
    }

    override fun handle(event: Event?): Boolean {
        return when (event) {
            is MapChangeEvent -> {
                event.map.layers.getByType(TiledMapTileLayer::class.java, tiledLayer)

                // world boundary chain shape to restrict movement within tiled map
                world.entity {
                    val width = event.map.width.toFloat()
                    val height = event.map.height.toFloat()

                    add<PhysicComponent> {
                        body = physicWorld.body(StaticBody) {
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
                            var cahin = chain(
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

            is CollisionDespawnEvent -> {
                processedCells.remove(event.cell)
                true
            }

            else -> false
        }
    }

    companion object {
        const val SPAWN_AREA_SIZE = 3
        private val log = logger<CollisionSpawnSystem>()
    }
}