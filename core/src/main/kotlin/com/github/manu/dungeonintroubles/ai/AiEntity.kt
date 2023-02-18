package com.github.manu.dungeonintroubles.ai

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.github.manu.dungeonintroubles.component.*
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World

/**
 * Almacena los datos para saber que estado se encuentra la entidad y funcione la StateMachine
 */
data class AiEntity(
    /**
     * Entidad del sistema
     */
    private val entity: Entity,
    /**
     * Mundo donde se encuentra la entidad
     */
    private val world: World,
    /**
     * Array de entidades con AnimationComponent
     */
    private val animationCmps: ComponentMapper<AnimationComponent> = world.mapper(),
    /**
     * Array de entidades con StateCompont
     */
    private val stateCmps: ComponentMapper<StateComponent> = world.mapper(),
    /**
     * Array de entidades con JumpComponent
     */
    private val jumpCmps: ComponentMapper<JumpComponent> = world.mapper(),
    /**
     * Array de entidades con MoveComponent
     */
    private val moveCmps: ComponentMapper<MoveComponent> = world.mapper(),
) {
    /**
     * Valor que mira si la entidad quiere volar
     */
    val wantsToFly: Boolean
        get() = jumpCmps[entity].sin != 0f

    /**
     * Valor que mira si la entidad quiere correr
     */
    val wantsToRun: Boolean
        get() = moveCmps[entity].cos != 0f

    /**
     * Cambia el estado de la entidad
     *
     * @param next El estado que se va a establecer
     * @param inmediateChange Para saber si se quiere cambiar de forma inmediata o esperar al sistema de estados
     */
    fun state(next: EntityState, inmediateChange: Boolean = false) {
        with(stateCmps[entity]) {
            nextState = next

            if (inmediateChange) {
                stateMachine.changeState(next)
            }
        }
    }

    /**
     * Cambia la animacion de la entidad
     *
     * @param type El tipo de animacion a la que se va a cambiar
     * @param mode El mode que se va a repoducir la animación, por defecto está en LOOP
     * @param resetAnimation Valor que si la animacion esta sin completar la reestablece al principio de la misma
     */
    fun animation(type: AnimationType, mode: PlayMode = PlayMode.LOOP, resetAnimation: Boolean = false) {
        with(animationCmps[entity]) {
            nextAnimation(type)
            this.playMode = mode

            if (resetAnimation) {
                stateTime = 0f
            }
        }
    }
}