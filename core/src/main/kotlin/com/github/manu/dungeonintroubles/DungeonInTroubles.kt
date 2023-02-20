package com.github.manu.dungeonintroubles

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.manu.dungeonintroubles.screen.MenuScreen
import com.github.manu.dungeonintroubles.ui.disposeSkin
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.disposeSafely

/**
 * Clase donde se inicia el juego y las principales variables
 */
class DungeonInTroubles : KtxGame<KtxScreen>() {
    /**
     * Donde se va a pintar la interfaz del juego
     */
    private val batch: Batch by lazy { SpriteBatch() }

    /**
     * Escenario que representa el juego con una vista de resolucion 16:9, con un valor lazy para iniciar con seguridad de hilos y si no se cnsiguiera iniciar se reintentaria
     */
    val gameStage by lazy { Stage(ExtendViewport(16f, 9f)) }
    /**
     * Escenario que representa la UI del juego con una vista de resolucion de 320px:180px, con un valor lazy para iniciar con seguridad de hilos y si no se cnsiguiera iniciar se reintentaria
     */
    val uiStage by lazy { Stage(ExtendViewport(320f, 180f), batch) }

    /**
     * Datos guardados del jugador del juego
     */
    lateinit var playerPrefs: Preferences
    /**
     * Datos guardados de los ajustes del juego
     */
    lateinit var settingsPrefs: Preferences

    /**
     * Donde se guarda las cadenas utilizadas del juego que pueden ser traducidas
     */
    lateinit var bundle: I18NBundle

    /**
     * Texture atlas para los graficos del juego
     */
    lateinit var textureAtlas: TextureAtlas

    /**
     * Se ejecuta al crear la clase inicializando las variables y añadiendo y estableciendo el MenuScreen
     */
    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        playerPrefs = Gdx.app.getPreferences("player")
        settingsPrefs = Gdx.app.getPreferences("settings")
        bundle = I18NBundle.createBundle(Gdx.files.internal("languages/MyBundle"))
        textureAtlas = TextureAtlas(Gdx.files.internal("graphics/gameObjects.atlas"))
        addScreen(MenuScreen(this))
        setScreen<MenuScreen>()
    }

    /**
     * Se ejecuta al redimensionar la ventana actualiza el tamaño de resolucion de los escenarios
     */
    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        gameStage.viewport.update(width, height, true)
        uiStage.viewport.update(width, height, true)
    }

    /**
     * Se ejecuta al cerrar la ventana para liberar los recursos usados
     */
    override fun dispose() {
        super.dispose()
        gameStage.disposeSafely()
        uiStage.disposeSafely()
        batch.disposeSafely()
        textureAtlas.dispose()
        disposeSkin()
    }



    companion object {
        // 16px = 1m in our physic world
        const val UNIT_SCALE = 1 / 16f
    }
}
