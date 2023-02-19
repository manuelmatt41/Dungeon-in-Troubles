package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.component.ImageComponent
import com.github.manu.dungeonintroubles.component.PlayerComponent
import com.github.manu.dungeonintroubles.event.MapChangeEvent
import com.github.quillraven.fleks.*
import ktx.log.logger
import ktx.tiled.height
import ktx.tiled.width

/**
 * Sistema que maneja la posicion de la camara respecto al personaje
 *
 * @property gameStage Escenario que representa el juego, se inicia de forma automatica
 * @property imgCmps Conjunto de entidades que tienen ImageComponent, se inicia de forma automatica
 */
@AllOf([PlayerComponent::class, ImageComponent::class])
class CameraSystem(
    @Qualifier("gameStage") private val gameStage: Stage,
    private val imgCmps: ComponentMapper<ImageComponent>,
) : EventListener, IteratingSystem() {
    /**
     * Ancho maxima de la camara
     */
    private var maxW = 0f

    /**
     * Altura maxima de la camara
     */
    private var maxH = 0f

    /**
     * Camara del escenario
     */
    private val camera = gameStage.camera

    /**
     * Por cada entidad mira la posicion y coloca la camara dependiendo de la posicion en el mapa de la entidad
     *
     * @param entity Entidad a ejecutar
     */
    override fun onTickEntity(entity: Entity) {
        with(imgCmps[entity]) {
            val viewW = camera.viewportWidth * 0.5f

            camera.position.set(
                (image.x + 5f).coerceIn(viewW, maxW - viewW),
                maxH * 0.5f,
                camera.position.z
            )
        }
    }

    /**
     * Se ejecuta al lanzar un evento y evalua si se contiene el evento y ejecuta una parte de codigo
     *
     * @param event Evento que se ha lanzado
     *
     * @return Devuelve true si ha cogido el evento sino devuelve false
     */
    override fun handle(event: Event?): Boolean {
        return when (event) {
            is MapChangeEvent -> {
                maxW = event.map.width.toFloat()
                maxH = event.map.height.toFloat()
                true
            }

            else -> false
        }
    }

    companion object {
        private val log = logger<CameraSystem>()
    }
}