package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import com.github.manu.dungeonintroubles.DungeonInTroubles.Companion.UNIT_SCALE
import com.github.manu.dungeonintroubles.component.*
import com.github.manu.dungeonintroubles.event.MapChangeEvent
import com.github.manu.dungeonintroubles.event.SpawnTrapEvent
import com.github.manu.dungeonintroubles.extension.physicCmpFromImage
import com.github.manu.dungeonintroubles.extension.physicCmpFromShape2D
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.app.gdxError
import ktx.box2d.box
import ktx.log.debug
import ktx.log.logger
import ktx.math.vec2
import ktx.tiled.*

@AllOf([SpawnComponent::class])
class EntitySpawnSystem(
    private val physicWorld: World,
    private val textureAtlas: TextureAtlas,
    private val spawnCmps: ComponentMapper<SpawnComponent>,
) : EventListener, IteratingSystem() {

    private val cachedConfigs = mutableMapOf<EntityType, SpawnConfiguration>()
    private val cachedSizes = mutableMapOf<AnimationModel, Vector2>()
    private val cachedTrapZones = mutableMapOf<String, MapLayer>()
    private var currentTrapZone: MapLayer? = null;

    override fun onTickEntity(entity: Entity) {
        with(spawnCmps[entity]) {
            val config = spawnCfg(name)
            val relativeSize = if (config.model != AnimationModel.NONE) size(config.model) else this.size

            world.entity {
                val imageCmp = add<ImageComponent> {
                    image = Image().apply {
                        setPosition(location.x, location.y)
                        setSize(relativeSize.x, relativeSize.y)
                        setScaling(Scaling.fill)
                    }
                }

                if (config.model != AnimationModel.NONE) {
                    add<AnimationComponent> {
                        nextAnimation(config.model, AnimationType.RUN)
                    }
                }

                val physicCmp = if (config.model != AnimationModel.NONE) {
                    physicCmpFromImage(
                        physicWorld,
                        imageCmp.image,
                        config.bodyType,
                    ) { physicComponent, width, height ->
                        val scalingWidth = width * config.physicScaling.x
                        val scalingHeight = height * config.physicScaling.y

                        physicComponent.offset.set(config.physicOffset)
                        physicComponent.size.set(scalingWidth, scalingHeight)

                        // hitbox
                        box(scalingWidth, scalingHeight, config.physicOffset) {
                            isSensor = true
                            userData = HIT_BOX_SENSOR
                            friction = 0f
                        }

                        if (config.bodyType != StaticBody) {
                            val collH = scalingHeight
                            val collOffset = vec2().apply { set(config.physicOffset) }
                            collOffset.y += scalingHeight * 0.5f

                            box(scalingWidth, scalingHeight, collOffset) {
                                friction = 0f
                            }
                        }
                    }
                } else {
                    physicCmpFromShape2D(
                        physicWorld,
                        location.x.toInt(),
                        location.y.toInt(),
                        shape
                    )
                }


                if (config.speedScaling > 0f) {
                    add<MoveComponent> {
                        speed = DEFAULT_SPEED_X
                        cos = 1f
                    }
                    add<JumpComponent> {
                        speed = DEFAULT_SPEED_Y
                    }
                }

                when (name) {
                    EntityType.PLAYER -> {
                        add<PlayerComponent>()
                    }

                    EntityType.TRAP -> {
                        add<TrapComponent>()
                    }

                    EntityType.COIN -> {
                        add<CoinComponent>()
                    }

                    EntityType.SPAWNPOINT -> {
                        log.debug { "SpawnPoint : $location" }
                        add<SpawnPointComponent>()
                    }

                    else -> gdxError("Non defined entity type $name")
                }
            }
        }

        world.remove(entity)
    }

    private fun spawnCfg(type: EntityType): SpawnConfiguration = cachedConfigs.getOrPut(type) {
        when (type) {
            EntityType.PLAYER -> SpawnConfiguration(
                AnimationModel.PLAYER,
                physicScaling = vec2(0.4f, 0.4f),
                physicOffset = vec2(0f, -5f * UNIT_SCALE)
            )

            EntityType.TRAP -> SpawnConfiguration(
                AnimationModel.TRAP,
                physicScaling = vec2(0.4f, 0.4f),
                speedScaling = 0f,
                bodyType = StaticBody
            )

            EntityType.COIN -> SpawnConfiguration(
                AnimationModel.COIN,
                physicScaling = vec2(0.4f, 0.4f),
                speedScaling = 0f,
                bodyType = StaticBody
            )

            EntityType.SPAWNPOINT -> SpawnConfiguration(
                AnimationModel.NONE,
            )

            else -> gdxError("Type $type has no SpawnCfg setup.")
        }
    }

    private fun size(model: AnimationModel) = cachedSizes.getOrPut(model) {
        val regions = textureAtlas.findRegions("${model.atlasKey}/${AnimationType.RUN.atlasKey}")

        if (regions.isEmpty) {
            gdxError("There are no regions for the ${AnimationType.RUN.atlasKey} animation of ${model.atlasKey}")
        }

        val firstFrame = regions.first()
        vec2(firstFrame.originalWidth * UNIT_SCALE, firstFrame.originalHeight * UNIT_SCALE)
    }

    private fun createEntitiesForLayers(layer: MapLayer, customLocation: Vector2 = vec2()) {
        layer.objects.forEach { mapObject ->
            val name = mapObject.name ?: gdxError("MapObject $mapObject does not have a name!")

            world.entity {
                add<SpawnComponent> {
                    this.name = EntityType.valueOf(name.uppercase())
                    this.location.set((mapObject.x + customLocation.x) * UNIT_SCALE, (mapObject.y + customLocation.y) * UNIT_SCALE)
                    log.debug { "Spawn in $location" }
                    if (this.name == EntityType.SPAWNPOINT) {
                        this.size.set(mapObject.width * UNIT_SCALE, mapObject.height * UNIT_SCALE)
                        this.shape = mapObject.shape
                    }
                }
            }
        }
    }

    override fun handle(event: Event?): Boolean {
        return when (event) {
            is MapChangeEvent -> {
                val entityLayer = event.map.layer("entities")
                createEntitiesForLayers(entityLayer)
                true
            }

            is SpawnTrapEvent -> {
                createEntitiesForLayers(cachedTrapZones.getOrPut(event.layerName) { event.trapMap.layer(event.layerName) }, event.location)
                true
            }

            else -> false
        }
    }

    companion object {
        private val log = logger<EntitySpawnSystem>()
        const val HIT_BOX_SENSOR = "Hitbox"
    }
}