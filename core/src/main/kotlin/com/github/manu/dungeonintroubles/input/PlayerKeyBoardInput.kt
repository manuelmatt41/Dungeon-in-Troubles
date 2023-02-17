package com.github.manu.dungeonintroubles.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys.P
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.component.JumpComponent
import com.github.manu.dungeonintroubles.component.PlayerComponent
import com.github.manu.dungeonintroubles.component.SpawnComponent
import com.github.manu.dungeonintroubles.event.GameResumeEvent
import com.github.manu.dungeonintroubles.event.PausePopUpEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.World
import ktx.app.KtxInputAdapter
import ktx.log.logger

class PlayerKeyBoardInput(
    world: World,
    private val gameStage: Stage,
    private val uiStage: Stage,
    private val jumpCmps: ComponentMapper<JumpComponent> = world.mapper(),
) : KtxInputAdapter {

    private var playerSin = 0f
    private val playerEntities =
        world.family(allOf = arrayOf(PlayerComponent::class), noneOf = arrayOf(SpawnComponent::class))

    init {
        val multiplexer = InputMultiplexer()
        multiplexer.addProcessor(uiStage)
        multiplexer.addProcessor(this)

        Gdx.input.inputProcessor = multiplexer
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

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == P) {
            gameStage.fire(PausePopUpEvent())
        }
        return true;
    }


    companion object {
        private val log = logger<PlayerKeyBoardInput>()
    }
}