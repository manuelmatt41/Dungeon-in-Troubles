package com.github.manu.dungeonintroubles.component

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.actor.FlipImage
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Qualifier

/**
 * Componente que representa a la imagen que va usar la entidad para dibujar en la pantalla
 */
class ImageComponent {
    /**
     * Imagen que reprensenta la entidad
     */
    lateinit var image: FlipImage

    companion object {
        /**
         * Clase que escucha cuando se añade o elimina una entidad con el componente imagen del sistema
         */
        class ImageComponentListener(
            /**
             * Escenario donde se va a dibujar la entidad
             */
            @Qualifier("gameStage") private val gameStage: Stage,
        ) : ComponentListener<ImageComponent> {
            /**
             * Se ejecuta cuando se añade la entidad con el componente imagen al sistema
             *
             * @param entity Entidad con el componente imagen
             * @param component Componente imagen
             */
            override fun onComponentAdded(entity: Entity, component: ImageComponent) {
                // Añade la imagen como actor del escenario que va a dibujar
                gameStage.addActor(component.image)
            }

            /**
             * Se ejecuta cuando se elimina la entidad con el componente imagen al sistema
             *
             * @param entity Entidad con el componente imagen
             * @param component Componente imagen
             */
            override fun onComponentRemoved(entity: Entity, component: ImageComponent) {
                // Elimina al actor del escenario
                gameStage.root.removeActor(component.image)
            }

        }
    }
}