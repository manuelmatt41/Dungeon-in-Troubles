package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.component.ImageComponent
import com.github.manu.dungeonintroubles.component.PlayerComponent
import com.github.manu.dungeonintroubles.event.MapChangeEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.quillraven.fleks.*
import ktx.log.logger

@AllOf([PlayerComponent::class])
class GenerateMapSystem(
    @Qualifier("gameStage") private val gameStage: Stage,
    private val imgCmps: ComponentMapper<ImageComponent>,
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        with(imgCmps[entity]) {
            if (image.x > 250f) { //TODO Reaction with a collision not with coordinate
                world.remove(entity)
                val nextMap = TmxMapLoader().load(Gdx.files.internal("map/${MathUtils.random(1, NUMBER_OF_MAPS)}.tmx").path())
                val trapMap = TmxMapLoader().load(Gdx.files.internal("map/traps.tmx").path())
                gameStage.fire(MapChangeEvent(nextMap, trapMap))
                log.debug { "Map change" }
            }
        }
    }

    companion object {
        private val log = logger<GenerateMapSystem>()
        const val NUMBER_OF_MAPS = 3
    }
}