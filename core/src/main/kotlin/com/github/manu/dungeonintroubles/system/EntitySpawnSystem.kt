package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.KinematicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import com.github.manu.dungeonintroubles.DungeonInTroubles.Companion.UNIT_SCALE
import com.github.manu.dungeonintroubles.actor.FlipImage
import com.github.manu.dungeonintroubles.component.*
import com.github.manu.dungeonintroubles.component.AnimationModel.*
import com.github.manu.dungeonintroubles.event.MapChangeEvent
import com.github.manu.dungeonintroubles.event.SpawnLayerObjectsEvent
import com.github.manu.dungeonintroubles.event.SpawnProjectilesEvent
import com.github.manu.dungeonintroubles.extension.physicCmpFromImage
import com.github.manu.dungeonintroubles.extension.physicCmpFromShape2D
import com.github.quillraven.fleks.*
import ktx.app.gdxError
import ktx.box2d.box
import ktx.log.logger
import ktx.math.vec2
import ktx.tiled.*

@AllOf([SpawnComponent::class])
class EntitySpawnSystem(
    @Qualifier("gameStage") private val gameStage: Stage,
    private val physicWorld: World,
    private val textureAtlas: TextureAtlas,
    private val spawnCmps: ComponentMapper<SpawnComponent>,
    private val playerCmps: ComponentMapper<PlayerComponent>
) : EventListener, IteratingSystem() {

    private val cachedConfigs = mutableMapOf<EntityType, SpawnConfiguration>()
    private val cachedSizes = mutableMapOf<AnimationModel, Vector2>()

    override fun onTickEntity(entity: Entity) {
        with(spawnCmps[entity]) {
            val config = spawnCfg(name)
            val relativeSize = if (config.model != NONE) size(config.model) else this.size

            world.entity {
                val imageCmp = add<ImageComponent> {
                    image = FlipImage().apply {
                        setPosition(location.x, location.y)
                        setSize(relativeSize.x, relativeSize.y)
                        setScaling(Scaling.fill)
                        flipX = config.model == FIREBALL
                    }
                }

                if (config.model != NONE) {
                    add<AnimationComponent> {
                        nextAnimation(config.model, AnimationType.RUN)
                    }
                }

                val physicCmp = if (config.model != NONE) {
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
                            isSensor =
                                config.model != FIREBALL && config.model != SLIME && config.model != SKELETON && config.model != DEMON
                            userData = HIT_BOX_SENSOR
                            friction = 0f
                        } //TODO Refactor this shit

                        if (config.model == PLAYER) {
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

                when (name) {
                    EntityType.PLAYER -> {
                        add<PlayerComponent>() {
                            with(playerCmps[entity]) {
                                this@add.coins = this.coins
                                this@add.meter = this.meter
                            }
                        }

                        add<MoveComponent> {
                            speed = DEFAULT_SPEED_X
                            cos = 1f
                        }

                        add<JumpComponent> {
                            speed = DEFAULT_SPEED_Y
                        }

                        add<StateComponent>()
                    }

                    EntityType.DEMON -> {
                        add<NpcComponent>()
                    }

                    EntityType.SLIME -> {
                        add<NpcComponent>()
                    }

                    EntityType.SKELETON -> {
                        add<NpcComponent>()
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

                    EntityType.PORTAL -> {}

                    EntityType.FIREBALL -> {
                        add<MoveComponent> {
                            speed = DEFAULT_SPEED_X
                            cos = -1f
                        }
                        physicCmp.body.gravityScale = 0f
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
                PLAYER,
                physicScaling = vec2(0.4f, 0.4f),
                physicOffset = vec2(0f, -5f * UNIT_SCALE)
            )

            EntityType.DEMON -> SpawnConfiguration(
                DEMON,
                physicScaling = vec2(0.4f, 0.4f),
                physicOffset = vec2(0f, -5f * UNIT_SCALE)
            )

            EntityType.SLIME -> SpawnConfiguration(
                SLIME,
                physicScaling = vec2(0.4f, 0.4f),
                physicOffset = vec2(0f, -5f * UNIT_SCALE)
            )

            EntityType.SKELETON -> SpawnConfiguration(
                SKELETON,
                physicScaling = vec2(0.4f, 0.4f),
                physicOffset = vec2(0f, -5f * UNIT_SCALE)
            )

            EntityType.TRAP -> SpawnConfiguration(
                TRAP,
                physicScaling = vec2(0.4f, 0.4f),
                bodyType = StaticBody
            )

            EntityType.COIN -> SpawnConfiguration(
                COIN,
                physicScaling = vec2(0.4f, 0.4f),
                bodyType = StaticBody
            )

            EntityType.PORTAL -> SpawnConfiguration(
                PORTAL,
                physicScaling = vec2(0.4f, 0.4f),
                bodyType = StaticBody
            )

            EntityType.SPAWNPOINT -> SpawnConfiguration(
                NONE,
            )

            EntityType.FIREBALL -> SpawnConfiguration(
                FIREBALL,
                physicScaling = vec2(0.4f, 0.4f),
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
                    this.location.set(
                        (mapObject.x + customLocation.x) * UNIT_SCALE,
                        (mapObject.y + customLocation.y) * UNIT_SCALE
                    )
                    log.debug { "Layer name: ${layer.name}" }
                    if (this.name == EntityType.SPAWNPOINT) {
                        this.size.set(mapObject.width * UNIT_SCALE, mapObject.height * UNIT_SCALE)
                        this.shape = mapObject.shape
                    }
                }
            }
        }
    }

    override fun handle(event: Event): Boolean {
        return when (event) {
            is MapChangeEvent -> {
                val entityLayer = event.map.layer("entities")
                createEntitiesForLayers(entityLayer)

                world.entity {
                    add<SpawnComponent> {
                        this.name = EntityType.PLAYER
                        this.location.set(
                            0f,
                            32f * UNIT_SCALE
                        )
                    }

                    add<PlayerComponent> {
                        this.coins = event.playerCmp.coins
                        this.meter = event.playerCmp.meter
                    }
                }
                true
            }

            is SpawnLayerObjectsEvent -> {
                createEntitiesForLayers(event.map.layer(event.layerName), event.location)
                true
            }

            is SpawnProjectilesEvent -> {
                for (i in 1..event.numberOfProjectiles) {
                    world.entity {
                        add<SpawnComponent> {
                            this.name = EntityType.FIREBALL
                            this.location.set(
                                gameStage.camera.position.x + gameStage.camera.viewportWidth,
                                MathUtils.random(1f, 144f) * UNIT_SCALE
                            )
                            log.debug { "Spawn projectile, Location: $location" }
                        }
                    }
                }
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