package com.github.manu.dungeonintroubles.system

import com.github.manu.dungeonintroubles.component.StateComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.log.logger

/**
 * Sistema que se encarga de cambiar las entidades entre estados
 *
 * @property stateCmps Conjunto de entidades con StateComponent, se inicializa de forma automatica
 */
@AllOf([StateComponent::class])
class StateSystem(
    private val stateCmps: ComponentMapper<StateComponent>,
) : IteratingSystem() {
    /**
     * Por cada entidad se comprueba que quiera cambiar el estado y en caso de querer, lo cambia
     *
     * @param entity Entidad a ejecutar
     */
    override fun onTickEntity(entity: Entity) {
        with(stateCmps[entity]) {
            if (nextState != stateMachine.currentState) {
                stateMachine.changeState(nextState)
            }

            stateMachine.update()
        }
    }

    companion object {
        private val log = logger<StateSystem>()
    }
}