package com.github.manu.dungeonintroubles.system

import com.github.manu.dungeonintroubles.component.StateComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.log.logger

@AllOf([StateComponent::class])
class StateSystem(
    private val stateCmps: ComponentMapper<StateComponent>,
) : IteratingSystem() {
    override fun onTickEntity(entity: Entity) {
        with(stateCmps[entity]) {
            if (nextState != stateMachine.currentState) {
                log.debug { "$nextState" }
                stateMachine.changeState(nextState)
            }

            stateMachine.update()
        }
    }

    companion object {
        private val log = logger<StateSystem>()
    }
}