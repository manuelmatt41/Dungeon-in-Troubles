package com.github.manu.dungeonintroubles.screen.debug

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.manu.dungeonintroubles.ui.disposeSkin
import com.github.manu.dungeonintroubles.ui.loadSkin
import com.github.manu.dungeonintroubles.ui.view.GameView
import com.github.manu.dungeonintroubles.ui.view.gameView
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.scene2d.actors

class UiScreen : KtxScreen {
    private val uiStage: Stage = Stage(ExtendViewport(320f, 180f))
    private lateinit var gameView: GameView

    init {
        loadSkin()
    }

    override fun resize(width: Int, height: Int) {
        uiStage.viewport.update(width, height, true)
    }

    override fun show() {
        uiStage.clear()
        uiStage.actors {
            gameView = gameView()
        }
        uiStage.isDebugAll = true
    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            hide()
            show()
        }

        uiStage.act()
        uiStage.draw()
    }

    override fun dispose() {
        uiStage.disposeSafely()
        disposeSkin()
    }
}