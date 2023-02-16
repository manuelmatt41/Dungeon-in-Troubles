package com.github.manu.dungeonintroubles.ai

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.github.manu.dungeonintroubles.component.*
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World

data class AiEntity(
    private val entity: Entity,
    private val world: World,
    private val animationCmps: ComponentMapper<AnimationComponent> = world.mapper(),
    private val stateCmps: ComponentMapper<StateComponent> = world.mapper(),
    private val jumpCmps: ComponentMapper<JumpComponent> = world.mapper(),
    private val moveCmps: ComponentMapper<MoveComponent> = world.mapper(),
) {
    val wantsToFly: Boolean
        get() = jumpCmps[entity].sin != 0f

    val wantsToRun: Boolean
        get() = moveCmps[entity].cos != 0f

    fun state(next: EntityState, inmediateChange: Boolean = false) {
        with(stateCmps[entity]) {
            nextState = next

            if (inmediateChange) {
                stateMachine.changeState(next)
            }
        }
    }

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