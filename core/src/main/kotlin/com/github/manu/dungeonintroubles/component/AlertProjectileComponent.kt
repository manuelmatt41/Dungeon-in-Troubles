package com.github.manu.dungeonintroubles.component

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Qualifier
import ktx.actors.plusAssign
import ktx.math.vec2

/**
 * Compoente que es la alerta de los proyectiles
 */
class AlertProjectileComponent {
    /**
     * Posicion que se en la pantalla donde se va a dibujar la alerta
     */
    var position: Vector2 = vec2()

    /**
     * Tiempo que lleva la alerta en pantalla
     */
    var time: Float = 0f

    /**
     * Tiempo que va a permanecer la alerta en pantalla
     */
    var lifeSpan: Float = 5f

    /**
     * Etiqueta que contiene el texto y la imagen de la alerta
     */
    lateinit var label: Label

    companion object {
        /**
         * Clase a la escucha de cuando se añade o elimina una entidad con el componente alerta de proyectiles al mundo establecido
         */
        class AlertProjectileComponentListener(
            /**
             * Escena en la que se va pintar la alerta
             */
            @Qualifier("uiStage") private val uiStage: Stage,
        ) : ComponentListener<AlertProjectileComponent> {
            /**
             * Se ejecuta cuando es añadida una entidad al sistema con el componente alerta de proyectiles
             *
             * @param entity Entidad añadida con el componente alerta de proyectiles
             * @param component Valor del compoente de proyectiles
             */
            override fun onComponentAdded(entity: Entity, component: AlertProjectileComponent) {
                // Añade la label del componente como actor para dibujar en el escenario
                uiStage.addActor(component.label)
                // Como la alerta es eliminado del escenario
                component.label += fadeOut(3f, Interpolation.fade)
            }

            /**
             * Se ejecuta cuando se elimina la entidad con el componente de alerta de proyectiles
             *
             * @param entity Entidad añadida con el componente alerta de proyectiles
             * @param component Valor del compoente de proyectiles
             */
            override fun onComponentRemoved(entity: Entity, component: AlertProjectileComponent) {
                //Elimina la label del componente del escenario
                uiStage.root.removeActor(component.label)
            }
        }
    }
}