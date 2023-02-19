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

/**
 * Sistema que se encarga de cambiar el mapa al alcanzar un posicion en el mapa
 *
 * @property gameStage Escenario que representa el juego, se inicializa de forma automatica
 * @property physicWorld Mundo de fisicas, se inicializa de forma automatica
 * @property imgCmps Conjunto de entidades con ImageComponent, se incicializa de forma automatica
 * @property playerCmps Conjuntos de entidades con PlayerComponent, se incializa de forma automatica
 */
@AllOf([PlayerComponent::class])
@NoneOf([SpawnComponent::class])
class GenerateMapSystem(
    @Qualifier("gameStage") private val gameStage: Stage,
    private val physicWorld: World,
    private val imgCmps: ComponentMapper<ImageComponent>,
    private val playerCmps: ComponentMapper<PlayerComponent>,
) : EventListener, IteratingSystem() {

    /**
     * Por cada entidad se comprueba la posicion y al llegar ala posicion establecida lanza un evento para cambiar el mapa
     *
     * @param entity Entidad a ejecutar
     *
     */
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

    /**
     * Inicia el nuevo mapa de forma aleatoria y lamza el evento para cargarla
     *
     * @param playerEntity Entidad que alcanzo la posicion para guardar sus datos y mandarlos al nuevo mapa
     */
    private fun changeMap(playerEntity: Entity) {
        world.removeAll()

        val nextMap =
            TmxMapLoader().load(Gdx.files.internal("map/${MathUtils.random(1, NUMBER_OF_MAPS)}.tmx").path())
        log.debug { "${playerCmps[playerEntity].actualSpeed}" }
        gameStage.fire(MapChangeEvent(nextMap, playerCmps[playerEntity]))
    }

    /**
     * Se ejecuta al lanzar un evento y comprueba si se coge el evento para ejecutar codigo
     */
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
                                vec2(width, 1.5f),
                                vec2(width, height),
                                vec2(0f, height)
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

        /**
         * Constante que es el numero de mapas que se pueden cargar
         */
        const val NUMBER_OF_MAPS = 3
    }
}