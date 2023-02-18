package com.github.manu.dungeonintroubles.component

import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.github.manu.dungeonintroubles.ai.AiEntity
import com.github.manu.dungeonintroubles.ai.EntityState
import com.github.manu.dungeonintroubles.ai.PlayerState
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World

/**
 * Componente que represeta el estado de la entidad en el sistema
 */
data class StateComponent(
    /**
     * Estado que se va a establecer, por defecto es RUN
     */
    var nextState: EntityState = PlayerState.RUN,
    /**
     * Maquina donde se va a ejecutar los cambios de esatdo de la entidad
     */
    val stateMachine: DefaultStateMachine<AiEntity, EntityState> = DefaultStateMachine()
) {
    companion object {
        /**
         * Clase que escucha cuando se añade o elimina un entidad en el sisema con el componente estado
         */
        class StateComponentListener(
            /**
             * Mundo que contiene las entidades
             */
            private val world: World,
        ) : ComponentListener<StateComponent> {
            /**
             * Se ejecuta cuando se añade una entidad al sistema con el componente estado
             *
             * @param entity Entidad con el componente
             * @param Componente estado
             */
            override fun onComponentAdded(entity: Entity, component: StateComponent) {
                //Añade a maquina de estados la entidad
                component.stateMachine.owner = AiEntity(entity, world)
            }
            /**
             * Se ejecuta cuando se elimina una entidad al sistema con el componente estado
             *
             * @param entity Entidad con el componente
             * @param Componente estado
             */
            override fun onComponentRemoved(entity: Entity, component: StateComponent) = Unit

        }
    }
}