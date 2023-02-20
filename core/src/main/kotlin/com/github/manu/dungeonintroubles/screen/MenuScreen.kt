package com.github.manu.dungeonintroubles.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.github.manu.dungeonintroubles.DungeonInTroubles
import com.github.manu.dungeonintroubles.component.ImageComponent.Companion.ImageComponentListener
import com.github.manu.dungeonintroubles.component.PhysicComponent.Companion.PhysicComponentListener
import com.github.manu.dungeonintroubles.component.PlayerComponent
import com.github.manu.dungeonintroubles.event.*
import com.github.manu.dungeonintroubles.extension.fire
import com.github.manu.dungeonintroubles.system.*
import com.github.manu.dungeonintroubles.ui.loadSkin
import com.github.manu.dungeonintroubles.ui.view.*
import com.github.quillraven.fleks.world
import ktx.actors.alpha
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import ktx.log.logger
import ktx.math.vec2
import ktx.scene2d.actors
import ktx.tiled.height

/**
 * Ventana que contine el menu principal y diferentes opciones
 */
class MenuScreen(val game: DungeonInTroubles) : KtxScreen, EventListener {
    /**
     * Escenario que representa la UI del juego, se inicia con la uiStage del jueg0
     */
    private val uiStage = game.uiStage

    /**
     * Escenario que representa el juego, se inicia con la gameStage del juego
     */
    private val gameStage = game.gameStage

    /**
     * Vista que contiene el menu
     */
    private var menuView: MenuView

    /**
     * Vista que contiene la tienda
     */
    private lateinit var storeView: StoreView

    /**
     * Vista que contiene los ajustes
     */
    private lateinit var settingsView: SettingsView

    /**
     * Vista que contiene los creditos
     */
    private lateinit var creditsView: CreditsView

    /**
     * Vista que contiene el tutorial
     */
    private lateinit var tutorialView: TutorialView

    /**
     * Datos que se guardan sobre el jugador
     */
    private val playerPrefs: Preferences = game.playerPrefs

    /**
     * Datos que se guardan sobre los ajustes
     */
    private val settingsPrefs: Preferences = game.settingsPrefs

    /**
     * Atlas de texturas que contiene imagenes
     */
    private val textureAtlas = game.textureAtlas

    /**
     * Mapa actual que se muestra de fondo
     */
    private var currentMap: TiledMap? = null

    /**
     * Mundo de físicas del fondo
     */
    private val physichWorld = createWorld(vec2(0f, -15f)).apply {
        autoClearForces = false
    }

    /**
     * Mundo de entidades que representa el fondo del menu principal
     */
    private val eWorld = world {
        injectables {
            add("uiStage", uiStage)
            add("gameStage", gameStage)
            add(textureAtlas)
            add(physichWorld)
            add(settingsPrefs)
        }

        components {
            add<ImageComponentListener>()
            add<PhysicComponentListener>()
        }

        systems {
            add<EntitySpawnSystem>()
            add<PhysicSystem>()
            add<MoveSystem>()
            add<AnimationSystem>()
            add<RenderSystem>()
            add<GenerateMapSystem>()
            add<AudioSystem>()
        }
    }

    /**
     * Inicia la clase cargando la skin de la UI ,añadiendo el input y los listeners de esta ventana
     */
    init {
        loadSkin()
        eWorld.systems.forEach { system ->
            if (system is EventListener) {
                gameStage.addListener(system)
                uiStage.addListener(system)
            }
        }

        Gdx.input.inputProcessor = uiStage

        uiStage.actors {
            menuView = menuView(bundle = game.bundle, prefs = playerPrefs)
            settingsView = settingsView(game.bundle, game.settingsPrefs) {
                alpha = 0f
                touchable = Touchable.disabled
            }
        }

        uiStage.addListener(this)
    }

    /**
     * Se ejecuta al redimensionar y actualiza el tamaño de los escenarios al redimensionar la ventana
     */
    override fun resize(width: Int, height: Int) {
        uiStage.viewport.update(width, height, true)
        gameStage.viewport.update(width, height, true)
    }

    /**
     * Establece el mapa que se va a cargar
     *
     * @param path Path del archivo tmx que contiene la informacion del mapa
     */
    private fun setMap(path: String) {
        currentMap?.disposeSafely()
        val newMap = TmxMapLoader().load(Gdx.files.internal(path).path())
        currentMap = newMap
        gameStage.camera.position.y = currentMap!!.height * 0.5f
        gameStage.fire(MapChangeEvent(newMap, PlayerComponent(coins = playerPrefs.getInteger("coins"))))
    }

    /**
     * Se ejecuta al enseñar el escenario
     */
    override fun show() {
        setMap("map/3.tmx")
    }

    /**
     * Se ejecuta al renderizar los frames de la ventana actualizando el mundo de entidades y dibujando la interfaz
     *
     * @param delta Tiempo transcurrido entre frames
     */
    override fun render(delta: Float) {
        val dt = delta.coerceAtMost(0.25f)
        eWorld.update(dt)
        uiStage.act()
        uiStage.draw()
    }

    /**
     * Se ejecuta al cerrar la ventana, liberando los recursos que estaba usando
     */
    override fun dispose() {
        super.dispose()
        eWorld.dispose()
        currentMap?.disposeSafely()
        physichWorld.disposeSafely()
    }

    /**
     * Escuha a diferentes eventos para ejecutar distintas opciones de cosidog dependiendo del evento
     *
     * @param event Evento lanzado
     *
     * @return Devuelve true si ha cogido el event sino false
     */
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

            is ShowSettingsEvent -> {
                menuView.touchable = Touchable.disabled
                menuView.alpha = 0f

                settingsView.alpha = 1f
                settingsView.touchable = Touchable.enabled
            }

            is HideSettingsEvent -> {
                menuView.touchable = Touchable.enabled
                menuView.alpha = 1f

                settingsView.alpha = 0f
                settingsView.touchable = Touchable.disabled
            }

            is ShowStoreEvent -> {
                menuView.touchable = Touchable.disabled
                menuView.alpha = 0f
                log.debug { "Store view" }
                uiStage.actors {
                    storeView = storeView(bundle = game.bundle, playerPrefs, settingsPrefs)
                }
            }

            is HideStoreEvent -> {
                menuView.touchable = Touchable.enabled
                menuView.alpha = 1f

                uiStage.actors.removeValue(storeView, true)
                uiStage.actors.removeValue(menuView, true)
                uiStage.actors {
                    menuView = menuView(bundle = game.bundle, prefs = playerPrefs)
                }
            }

            is ShowCreditsEvent -> {
                menuView.touchable = Touchable.disabled
                menuView.alpha = 0f

                uiStage.actors {
                    creditsView = creditsView(game.bundle, game.settingsPrefs)
                }
            }

            is HideCreditsEvent -> {
                menuView.touchable = Touchable.enabled
                menuView.alpha = 1f

                uiStage.actors.removeValue(creditsView, true)
            }

            is ShowTutorialEvent -> {
                menuView.touchable = Touchable.disabled
                menuView.alpha = 0f
                gameStage.alpha = 0.2f

                uiStage.actors {
                    tutorialView = tutorialView(game.bundle, game.settingsPrefs)
                }
            }

            is HideTutorialEvent -> {
                menuView.touchable = Touchable.enabled
                menuView.alpha = 1f
                gameStage.alpha = 1f

                uiStage.actors.removeValue(tutorialView, true)
            }

            is ExitGameEvent -> {
                playerPrefs.flush()
                Gdx.app.exit()
            }

            else -> return false
        }
        return true;
    }

    companion object {
        private val log = logger<MenuScreen>()
    }
}