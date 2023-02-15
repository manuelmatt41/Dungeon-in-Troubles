package com.github.manu.dungeonintroubles

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.manu.dungeonintroubles.screen.MenuScreen
import com.github.manu.dungeonintroubles.ui.disposeSkin
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.disposeSafely

class DungeonInTroubles : KtxGame<KtxScreen>() {
    private val batch: Batch by lazy { SpriteBatch() }
    val gameStage by lazy { Stage(ExtendViewport(16f, 9f)) }
    val uiStage by lazy { Stage(ExtendViewport(320f, 180f), batch) }
    lateinit var prefs: Preferences
    lateinit var bundle: I18NBundle
    var paused = false

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        prefs = Gdx.app.getPreferences("preferences")
        bundle = I18NBundle.createBundle(Gdx.files.internal("languages/MyBundle"))
        addScreen(MenuScreen(this))
        setScreen<MenuScreen>()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        gameStage.viewport.update(width, height, true)
        uiStage.viewport.update(width, height, true)
    }

    override fun dispose() {
        super.dispose()
        gameStage.disposeSafely()
        uiStage.disposeSafely()
        batch.disposeSafely()
        disposeSkin()
        prefs.flush()
    }



    companion object {
        // 16px = 1m in our physic world
        const val UNIT_SCALE = 1 / 16f
    }
}
