package com.github.manu.dungeonintroubles.input

import com.badlogic.gdx.Gdx
import com.github.manu.dungeonintroubles.component.MoveComponent
import com.github.manu.dungeonintroubles.component.PlayerComponent
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.World
import ktx.app.KtxInputAdapter
import ktx.log.logger

class PlayerKeyBoardInput(
    world: World,
    private val moveCmps: ComponentMapper<MoveComponent> = world.mapper(),
) : KtxInputAdapter {

    private var playerSin = -1f
    private val playerEntities = world.family(allOf = arrayOf(PlayerComponent::class))

    init {
        Gdx.input.inputProcessor = this
    }
    private fun updatePlayerMovement() {
        playerEntities.forEach { player ->
            with(moveCmps[player]) {
                sin = playerSin
            }
        }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        playerSin = 1f
        updatePlayerMovement()
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        playerSin = -1f
        updatePlayerMovement()
        return true
    }

    companion object {
        private val log = logger<PlayerKeyBoardInput>()
    }
}