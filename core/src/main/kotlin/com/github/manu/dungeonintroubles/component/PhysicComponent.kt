package com.github.manu.dungeonintroubles.component

import com.badlogic.gdx.physics.box2d.Body
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import ktx.log.logger
import ktx.math.vec2

/**
 * Componente que representa las fisicas que tiene la entidad en el undo de físicas del juego
 */
class PhysicComponent {
    /**
     * Posición anterior que tenia la entidad, por defecto es 0
     */
    val prevPosition = vec2()

    /**
     * Impulso que lleva la entidad, por defecto es 0
     */
    val impulse = vec2()

    /**
     * Posicion del cuerpo del mundo de fisicas representado en la pantalla, por defecto es 0
     */
    val offset = vec2()

    /**
     * Tamaño del cuerpo en el sistema, por defecto es 0
     */
    val size = vec2()

    /**
     * Cuerpo que se establce cuando la entidad aparace en el sistema
     */
    lateinit var body: Body

    companion object {
        /**
         * Variable para establcer log en la clase PhysicComponent por consola
         */
        private val log = logger<PhysicComponent>()

        /**
         * Clase que escucha si se añade o elimina una entidad del sistema con el componente de físicas
         */
        class PhysicComponentListener(
        ) : ComponentListener<PhysicComponent> {
            /**
             * Se ejecuta si se añade una entidad con el componente de físicas
             *
             * @param entity Entidad con el componente de físicas
             * @param component Componente de físicas
             */
            override fun onComponentAdded(entity: Entity, component: PhysicComponent) {
                //Establece el mismo nombre de la entidad en el cuerpo que representa sus físicas
                component.body.userData = entity
            }

            /**
             * Se ejecuta si se elimna una entidad con el componente de físicas
             *
             * @param entity Entidad con el componente de físicas
             * @param component Componente de físicas
             */
            override fun onComponentRemoved(entity: Entity, component: PhysicComponent) {
                val body = component.body
                // Destruye el cuerpo del mundo de fisicas
                body.world.destroyBody(body)
                // Esatblace el nombre del cuerpo como null
                body.userData = null
            }

        }
    }
}