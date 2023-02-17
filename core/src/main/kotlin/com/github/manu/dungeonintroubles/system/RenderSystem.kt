package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.DungeonInTroubles
import com.github.manu.dungeonintroubles.DungeonInTroubles.Companion.UNIT_SCALE
import com.github.manu.dungeonintroubles.component.AnimationModel
import com.github.manu.dungeonintroubles.component.ImageComponent
import com.github.manu.dungeonintroubles.event.GetCoinEvent
import com.github.manu.dungeonintroubles.event.MapChangeEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.manu.dungeonintroubles.screen.GameScreen
import com.github.quillraven.fleks.*
import ktx.graphics.use
import ktx.log.logger
import ktx.tiled.forEachLayer

@AllOf([ImageComponent::class])
class RenderSystem(
    @Qualifier("gameStage") private val gameStage: Stage,
    @Qualifier("uiStage") private val uiStage: Stage,
    private val textureAtlas: TextureAtlas,
    private val imgCmps: ComponentMapper<ImageComponent>,
) : EventListener, IteratingSystem() {

    private val bgdLayers = mutableListOf<TiledMapTileLayer>()
    private val floorLayers = mutableListOf<TiledMapTileLayer>()
    private val decorationLayers = mutableListOf<TiledMapTileLayer>()


    private val mapRenderer = OrthogonalTiledMapRenderer(null, UNIT_SCALE, gameStage.batch)
    private val orthoCam = gameStage.camera as OrthographicCamera


    override fun onTick() {
        super.onTick()

        with(gameStage) {
            viewport.apply()

            AnimatedTiledMapTile.updateAnimationBaseTime()
            mapRenderer.setView(orthoCam)

            if (bgdLayers.isNotEmpty()) {
                gameStage.batch.use(orthoCam.combined) {
                    bgdLayers.forEach { mapRenderer.renderTileLayer(it) }
                }
            }

            if (floorLayers.isNotEmpty()) {
                gameStage.batch.use(orthoCam.combined) {
                    floorLayers.forEach { mapRenderer.renderTileLayer(it) }
                }
            }

            if (decorationLayers.isNotEmpty()) {
                gameStage.batch.use(orthoCam.combined) {
                    decorationLayers.forEach { mapRenderer.renderTileLayer(it) }
                }
            }


            gameStage.run {
                act(deltaTime)
                draw()
            }
        }

        with(uiStage) {
            viewport.apply()
            act(deltaTime)
            draw()
        }


    }

    override fun onTickEntity(entity: Entity) {
        imgCmps[entity].image.toFront()
    }

    override fun handle(event: Event?): Boolean {
        return when (event) {
            is MapChangeEvent -> {
                log.debug { "The map has been changed" }

                mapRenderer.map = event.map

                bgdLayers.clear()
                floorLayers.clear()
                decorationLayers.clear()

                event.map.forEachLayer<TiledMapTileLayer> { layer ->
                    when (layer.name) {
                        BGD_LAYER -> bgdLayers.add(layer)
                        FLOOR_LAYER -> floorLayers.add(layer)
                        DECORATION_LAYER -> decorationLayers.add(layer)
                    }
                }
                true
            }

            else -> false
        }
    }

    companion object {
        private val log = logger<RenderSystem>()
        const val BGD_LAYER = "bgd"
        const val FLOOR_LAYER = "floor"
        const val DECORATION_LAYER = "decoration"
    }
}