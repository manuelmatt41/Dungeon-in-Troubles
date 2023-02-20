package com.github.manu.dungeonintroubles.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.github.manu.dungeonintroubles.DungeonInTroubles
import com.github.manu.dungeonintroubles.component.AlertProjectileComponent.Companion.AlertProjectileComponentListener
import com.github.manu.dungeonintroubles.component.ImageComponent.Companion.ImageComponentListener
import com.github.manu.dungeonintroubles.component.PhysicComponent.Companion.PhysicComponentListener
import com.github.manu.dungeonintroubles.component.PlayerComponent
import com.github.manu.dungeonintroubles.component.StateComponent.Companion.StateComponentListener
import com.github.manu.dungeonintroubles.event.*
import com.github.manu.dungeonintroubles.extension.fire
import com.github.manu.dungeonintroubles.input.PlayerKeyBoardInput
import com.github.manu.dungeonintroubles.system.*
import com.github.manu.dungeonintroubles.ui.loadSkin
import com.github.manu.dungeonintroubles.ui.model.GameModel
import com.github.manu.dungeonintroubles.ui.view.GameView
import com.github.manu.dungeonintroubles.ui.view.SettingsView
import com.github.manu.dungeonintroubles.ui.view.gameView
import com.github.manu.dungeonintroubles.ui.view.settingsView
import com.github.quillraven.fleks.world
import ktx.actors.alpha
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import ktx.log.logger
import ktx.math.vec2
import ktx.scene2d.actors

/**
 * Ventana que contiene el juego principal y sus mecanicas
 * @property game Datos del juego
 *
 * @constructor Crea una GameScren vacio
 */
class GameScreen(val game: DungeonInTroubles) : KtxScreen, EventListener {
    /**
     * Escenario que representa el juego, se inicia con la gameStage del juego
     */
    private val gameStage: Stage = game.gameStage

    /**
     * Escenario que representa la UI del juego, se inicia con la uiStage del juego
     */
    private val uiStage: Stage = game.uiStage

    /**
     * Atlas de texturas para coger las imagenes que se van  a utilizar, se inicia con el texuteAtlas del juego
     */
    private val textureAtlas = game.textureAtlas

    /**
     * El mapa que esta cargado en ese mismo momento, por defecto es null
     */
    private var currentMap: TiledMap? = null

    /**
     * Mundo que representa las fisicas del juego, se crea por defecto con gravedad
     */
    private val physichWorld = createWorld(vec2(0f, -15f)).apply {
        autoClearForces = false
    }

    /**
     * Datos que se guardan internamente del jugador, se inicializa con las preferencias cargadas en el juegp
     */
    private val playerPrefs = game.playerPrefs

    /**
     * Datos que se guardar internamente de los ajustes, se incializa con las preferencias cargadas en el juego
     */
    private val settingsPrefs = game.settingsPrefs

    /**
     * Vista de la interfaz del juego
     */
    private var gameView: GameView

    /**
     * Vista de los ajustes del juego
     */
    private var settingsView: SettingsView

    /**
     * Mundo de entidades que se va usar en la ventana
     */
    private val eWorld = world {
        //Inyecta en el sistema variables comunes entre sistemas que mapea de forma automatica
        injectables {
            add("gameStage", gameStage)
            add("uiStage", uiStage)
            add(textureAtlas)
            add(physichWorld)
            add(settingsPrefs)
        }

        //Añade los componentes que se encargan de esuchar cuando se añade o elimina entidades con diferentes componentes en el sistema
        components {
            add<ImageComponentListener>()
            add<PhysicComponentListener>()
            add<StateComponentListener>()
            add<AlertProjectileComponentListener>()
        }

        // Se añade los sistemas que hacen funcionar distintas mecanicas o funciones del juego de forma separada
        systems {
            add<EntitySpawnSystem>()
            add<SpawnProjectilesSystem>()
            add<AlertProjectileSystem>()
            add<PhysicSystem>()
            add<JumpMoveSystem>()
            add<MoveSystem>()
            add<AnimationSystem>()
            add<StateSystem>()
            add<CameraSystem>()
            add<RenderSystem>()
            add<ParticleSystem>()
            add<GenerateMapSystem>()
            add<AudioSystem>()
            add<DespawnSystem>()
            add<DebugSystem>()
        }
    }

    init {
        //Carga la skin que van a usar los diferentes componentes de la interfaz
        loadSkin()

        // Añade al escenario del juego y interfaz los diferentes sistemas que actuan como oyentes de diferentes eventos
        eWorld.systems.forEach { system ->
            if (system is EventListener) {
                gameStage.addListener(system)
                uiStage.addListener(system)
            }
        }
        //Mira si esta el giroscopio activado para establer controlador de entradas
        if (!settingsPrefs.getBoolean("gyroscope")) {
            PlayerKeyBoardInput(eWorld, gameStage, uiStage)
        }

        //Añade al escenario de UI los actores a las vistas que actuan como UI
        uiStage.actors {
            gameView = gameView(GameModel(eWorld, uiStage), game.bundle, playerPrefs, settingsPrefs)
            settingsView = settingsView(bundle = game.bundle, prefs = settingsPrefs)
        }
        //Añade al escenario del juego y UI esta msima clase
        gameStage.addListener(this)
        uiStage.addListener(this)
    }

    /**
     * Se ejecuta al aparecer la pantalla
     */
    override fun show() {
        //Lanza el evebnto para recuperar las monedas guardadas y establece el mapaque se va a jugar
        uiStage.fire(GetCoinEvent(playerPrefs.getInteger("coins")))
        setMap("map/3.tmx")
    }

    /**
     * Pausa diferentes sistemas del mundo de entidades para parar el juego
     */
    private fun pauseWorld(pause: Boolean) {
        //Sistemas que van a seguir ejecutandose
        val mandatorySystems = setOf(
            AnimationSystem::class, CameraSystem::class, RenderSystem::class, DebugSystem::class
        )
        //Para el resto de sistemas que no se ha especificado
        eWorld.systems.filter { it::class !in mandatorySystems }.forEach { it.enabled = !pause }
    }

    /**
     * Se ejecuta cuando entra en estado de pausa la ventana, y pausa el juego
     */
    override fun pause() = pauseWorld(true) // TODO Do popup on exit the app

    /**
     * Se ejecuta al volver entrar en la ventana y reanuda el juego donde se encontraba
     */
    override fun resume() {
        //Quita la puasa solo si no estaba en menu de pausa o de los ajustes
        if (gameView.pausePopup.alpha != 1f || settingsView.alpha != 1f) {
            pauseWorld(false)
        }
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
        gameStage.fire(MapChangeEvent(newMap, PlayerComponent(coins = playerPrefs.getInteger("coins"))))
    }

    /**
     * Se ejecuta cada frame para renderizar la ventana
     *
     * @param delta Tiempo transcurrido entre frames
     */
    override fun render(delta: Float) {
        //Establece un valor maximo al deltaTime y actualiza el mundo entidades con ese valor
        val dt = delta.coerceAtMost(0.25f)
        eWorld.update(dt)
    }

    /**
     * Se eejecuta al redimensionar la ventana
     */
    override fun resize(width: Int, height: Int) {
        //Actualiza el tamaño de los escenarios para reescalar las imagenes
        gameStage.viewport.update(width, height, true)
        uiStage.viewport.update(width, height, true)
    }

    /**
     * Se ejecuta al cerrar la ventana
     */
    override fun dispose() {
        //Libera recursos de los objetos que se guardan en memoria
        eWorld.dispose()
        currentMap.disposeSafely()
        physichWorld.disposeSafely()
    }

    companion object {
        /**
         * Valor para hacer mensajes por consola
         */
        private val log = logger<GameScreen>()
    }

    /**
     * Se ejecuta caundo se ha lanzado un evento y mirar si tiene el evento para ejecutarlo
     *
     * @param event Evento lanzado
     */
    override fun handle(event: Event): Boolean {
        when (event) {
            is GameResumeEvent -> {
                PlayerKeyBoardInput(eWorld, gameStage, uiStage)
                gameView.resume()
                resume()
            }

            is PausePopUpEvent -> {
                pause()
                Gdx.input.inputProcessor = uiStage
                gameView.pause()
            }

            is DeadPopUpEvent -> {
                pause()
                Gdx.input.inputProcessor = uiStage
                gameView.death()

                // Guarda los datos del jugador al perder y solo guarda la distancia si es mejor que la anterior guardada
                if (event.playerCmp.distance > playerPrefs.getFloat("distance")) {
                    playerPrefs.putFloat("distance", event.playerCmp.distance)
                }
                playerPrefs.putInteger("coins", event.playerCmp.coins)
                log.debug { "Coins save: ${playerPrefs.getInteger("coins")}" }
                playerPrefs.flush()
            }

            is SetMenuScreenEvent -> {
                log.debug { "Set menu screen" }

                gameStage.clear()
                uiStage.clear()

                game.addScreen(MenuScreen(game))
                game.setScreen<MenuScreen>()

                game.removeScreen<GameScreen>()
                super.hide()
                this.dispose()
            }

            is ShowSettingsEvent -> {
                pause()
                gameView.touchable = Touchable.disabled
                gameView.alpha = 0f

                settingsView.alpha = 1f
                settingsView.touchable = Touchable.enabled
            }

            is HideSettingsEvent -> {
                gameView.touchable = Touchable.enabled
                gameView.alpha = 1f

                settingsView.alpha = 0f
                settingsView.touchable = Touchable.disabled
            }

            is SetGameEvent -> {
                gameStage.clear()
                uiStage.clear()


                game.removeScreen<GameScreen>()
                game.addScreen(GameScreen(game))
                game.setScreen<GameScreen>()
                super.hide()
                this.dispose()
            }

            else -> return false
        }

        return true
    }

}