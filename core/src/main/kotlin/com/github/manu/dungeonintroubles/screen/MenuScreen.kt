package com.github.manu.dungeonintroubles.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.github.manu.dungeonintroubles.DungeonInTroubles
import com.github.manu.dungeonintroubles.event.ExitGameEvent
import com.github.manu.dungeonintroubles.event.SetGameEvent
import com.github.manu.dungeonintroubles.ui.disposeSkin
import com.github.manu.dungeonintroubles.ui.loadSkin
import com.github.manu.dungeonintroubles.ui.view.MenuView
import com.github.manu.dungeonintroubles.ui.view.menuView
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.scene2d.actors

class MenuScreen(val game: DungeonInTroubles) : KtxScreen, EventListener {
    private val uiStage = game.uiStage
    private val gameStage = game.gameStage
    private lateinit var menuView: MenuView

    init {
        loadSkin()
        Gdx.input.inputProcessor = uiStage
        uiStage.addListener(this)
        uiStage.actors {
            menuView(bundle = game.bundle, stage = uiStage)
        }
        uiStage.isDebugAll = true
    }

    override fun resize(width: Int, height: Int) {
        uiStage.viewport.update(width, height, true)
    }

    override fun show() {
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
        super.dispose()
    }

    override fun handle(event: Event?): Boolean {
        when (event) {
            is SetGameEvent -> {
                gameStage.clear()
                uiStage.clear()

                game.addScreen(GameScreen(game))
                game.setScreen<GameScreen>()

                game.removeScreen<MenuScreen>()
                super.hide()
                this.dispose()
            }

            is ExitGameEvent -> {
                Gdx.app.exit()
            }

            else -> return false
        }
        return true;
    }
}