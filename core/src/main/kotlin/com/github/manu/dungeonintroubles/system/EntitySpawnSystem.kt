package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.math.MathUtils
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
import com.github.manu.dungeonintroubles.extension.physicCmpFromImage
import com.github.manu.dungeonintroubles.system.GenerateMapSystem.Companion.LAYER_POSIBILITIES
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.app.gdxError
import ktx.box2d.box
import ktx.math.vec2
import ktx.tiled.layer
import ktx.tiled.x
import ktx.tiled.y

@AllOf([SpawnComponent::class])
class EntitySpawnSystem(
    private val physicWorld: World,
    private val textureAtlas: TextureAtlas,
    private val spawnCmps: ComponentMapper<SpawnComponent>,
) : EventListener, IteratingSystem() {

    private val cachedConfigs = mutableMapOf<EntityType, SpawnConfiguration>()
    private val cachedSizes = mutableMapOf<AnimationModel, Vector2>()

    override fun onTickEntity(entity: Entity) {
        with(spawnCmps[entity]) {
            val config = spawnCfg(name)
            val relativeSize = size(config.model)

            world.entity {
                val imageCmp = add<ImageComponent> {
                    image = Image().apply {
                        setPosition(location.x, location.y)
                        setSize(relativeSize.x, relativeSize.y)
                        setScaling(Scaling.fill)
                    }
                }

                add<AnimationComponent> {
                    nextAnimation(config.model, AnimationType.RUN)
                }

                val physicCmp = physicCmpFromImage(
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
                        isSensor = config.bodyType != StaticBody
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

                    else -> gdxError("Non defined entity type $name")
                }

                if (config.bodyType != StaticBody) {
                    //such entities will create/remove collision objects
                    add<CollisionComponent>()
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

    private fun createEntitiesForLayers(layer: MapLayer) {
        layer.objects.forEach { mapObject ->
            val name = mapObject.name ?: gdxError("MapObject $mapObject does not have a name!")

            world.entity {
                add<SpawnComponent> {
                    this.name = EntityType.valueOf(name.uppercase())
                    this.location.set(mapObject.x * UNIT_SCALE, mapObject.y * UNIT_SCALE)
                }
            }
        }
    }

    override fun handle(event: Event?): Boolean {
        return when (event) {
            is MapChangeEvent -> {
                val entityLayer = event.map.layer("entities")
                val trapLayer1 = event.trapMap.layer("traps_zone1_${MathUtils.random(1, LAYER_POSIBILITIES)}")
                val trapLayer2 = event.trapMap.layer("traps_zone2_${MathUtils.random(1, LAYER_POSIBILITIES)}")
                val trapLayer3 = event.trapMap.layer("traps_zone3_${MathUtils.random(1, LAYER_POSIBILITIES)}")
                val coinLayer1 = event.trapMap.layer("coins_zone1_${MathUtils.random(1, LAYER_POSIBILITIES)}")
                val coinLayer2 = event.trapMap.layer("coins_zone2_${MathUtils.random(1, LAYER_POSIBILITIES)}")

                createEntitiesForLayers(entityLayer)
                createEntitiesForLayers(trapLayer1)
                createEntitiesForLayers(trapLayer2)
                createEntitiesForLayers(trapLayer3)
                createEntitiesForLayers(coinLayer1)
                createEntitiesForLayers(coinLayer2)

                true
            }

            else -> false
        }
    }

    companion object {
        const val HIT_BOX_SENSOR = "Hitbox"
        const val AI_SENSOR = "AiSensor"
    }
}