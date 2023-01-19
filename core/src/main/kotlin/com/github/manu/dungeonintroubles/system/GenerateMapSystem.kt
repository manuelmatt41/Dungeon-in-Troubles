package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.component.*
import com.github.manu.dungeonintroubles.event.CrossPortalEvent
import com.github.manu.dungeonintroubles.event.DeathEvent
import com.github.manu.dungeonintroubles.event.MapChangeEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.quillraven.fleks.*
import ktx.log.logger

@AllOf([PlayerComponent::class])
class GenerateMapSystem(
    @Qualifier("gameStage") private val gameStage: Stage,
    private val imgCmps: ComponentMapper<ImageComponent>,
    private val despaswnCmps: ComponentMapper<DespawnComponent>,
    private val playerCmps: ComponentMapper<PlayerComponent>,
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        with(imgCmps[entity]) {
            if (entity in playerCmps) {
                if (image.x > 250f) { //TODO Reaction with a collision not with coordinate
                    gameStage.fire(CrossPortalEvent())

                    changeMap()
                }

                if (entity in despaswnCmps) {
                    gameStage.fire(DeathEvent())

                    changeMap()
                }
            }
        }
    }

    private fun changeMap() {
        world.removeAll()

        val nextMap =
            TmxMapLoader().load(Gdx.files.internal("map/${MathUtils.random(1, NUMBER_OF_MAPS)}.tmx").path())
        val trapMap = TmxMapLoader().load(Gdx.files.internal("map/traps.tmx").path())
        gameStage.fire(MapChangeEvent(nextMap, trapMap))
        log.debug { "Map change" }
    }

    companion object {
        private val log = logger<GenerateMapSystem>()
        const val NUMBER_OF_MAPS = 3
        const val LAYER_POSIBILITIES = 3
    }
}