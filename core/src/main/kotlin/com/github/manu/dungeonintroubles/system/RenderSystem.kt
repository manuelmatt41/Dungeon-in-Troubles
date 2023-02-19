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

/**
 * Sistema que se encarga de renderizar las entidades con ImageCompoent
 *
 * @property gameStage Escenario que representa el juego, se inicializa de forma automatica
 * @property uiStage Escenario que representa la interfaz del juego, se inicializa de forma automatica
 * @property imgCmps Conjunto de entidades con ImageComponent, se inicializa de forma automatica
 */
@AllOf([ImageComponent::class])
class RenderSystem(
    @Qualifier("gameStage") private val gameStage: Stage,
    @Qualifier("uiStage") private val uiStage: Stage,
    private val imgCmps: ComponentMapper<ImageComponent>,
) : EventListener, IteratingSystem() {

    /**
     * Capas de celdas del fondo del mapa
     */
    private val bgdLayers = mutableListOf<TiledMapTileLayer>()

    /**
     * Capas de celdas del suelo del mapa
     */
    private val floorLayers = mutableListOf<TiledMapTileLayer>()

    /**
     * Capas de celdas de decoracion del mapa
     */
    private val decorationLayers = mutableListOf<TiledMapTileLayer>()

    /**
     * Mapa rendereizado en el juego
     */
    private val mapRenderer = OrthogonalTiledMapRenderer(null, UNIT_SCALE, gameStage.batch)

    /**
     * Camara del escenario del juego
     */
    private val orthoCam = gameStage.camera as OrthographicCamera

    /**
     * Por cada ejecucion del sistema se pinta los escenarios y sus actores respectivamente
     */
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

    /**
     * Por cada entidad del sistema se pone al frente en orden
     */
    override fun onTickEntity(entity: Entity) {
        imgCmps[entity].image.toFront()
    }

    /**
     * Se ejecuta cuando se lanza un evento y comprueba si lo tiene para ejecutar codigo
     */
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

        /**
         * Nombre de la capa del fondo
         */
        const val BGD_LAYER = "bgd"

        /**
         * Nombre de la capa del suelo
         */
        const val FLOOR_LAYER = "floor"

        /**
         * Nombre de la capa de decoracion
         */
        const val DECORATION_LAYER = "decoration"
    }
}