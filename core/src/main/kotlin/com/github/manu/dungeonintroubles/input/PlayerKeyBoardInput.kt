package com.github.manu.dungeonintroubles.input

import com.badlogic.gdx.Gdx
import com.github.manu.dungeonintroubles.component.JumpComponent
import com.github.manu.dungeonintroubles.component.MoveComponent
import com.github.manu.dungeonintroubles.component.PlayerComponent
import com.github.manu.dungeonintroubles.component.SpawnComponent
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.NoneOf
import com.github.quillraven.fleks.World
import ktx.app.KtxInputAdapter
import ktx.log.logger

class PlayerKeyBoardInput(
    world: World,
    private val jumpCmps: ComponentMapper<JumpComponent> = world.mapper(),
) : KtxInputAdapter {

    private var playerSin = 0f
    private val playerEntities = world.family(allOf = arrayOf(PlayerComponent::class), noneOf = arrayOf(SpawnComponent::class))

    init {
        Gdx.input.inputProcessor = this
    }
    private fun updatePlayerMovement() {
        playerEntities.forEach { player ->
            with(jumpCmps[player]) {
                sin = playerSin
            }
//            with(moveCmps[player]) {
//                cos = playerCos
//            }
        }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        playerSin = 1f
        updatePlayerMovement()
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        playerSin = 0f
        updatePlayerMovement()
        return true
    }

    companion object {
        private val log = logger<PlayerKeyBoardInput>()
    }
}