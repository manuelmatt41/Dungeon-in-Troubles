package com.github.manu.dungeonintroubles

import com.badlogic.gdx.Application
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.github.manu.dungeonintroubles.screen.GameScreen
import com.github.manu.dungeonintroubles.screen.debug.UiScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen

class DungeonInTroubles : KtxGame<KtxScreen>() {
    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG

        addScreen(GameScreen())
        addScreen(UiScreen())

        setScreen<GameScreen>()
//        setScreen<UiScreen>()
    }

    companion object {
        const val UNIT_SCALE = 1 / 16f
    }
}
