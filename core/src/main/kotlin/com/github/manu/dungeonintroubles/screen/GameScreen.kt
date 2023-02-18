package com.github.manu.dungeonintroubles.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
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

class GameScreen(val game: DungeonInTroubles) : KtxScreen, EventListener {
    private val gameStage: Stage = game.gameStage
    private val uiStage: Stage = game.uiStage
    private val textureAtlas = game.textureAtlas
    private var currentMap: TiledMap? = null
    private val physichWorld = createWorld(vec2(0f, -15f)).apply {
        autoClearForces = false
    }
    private val playerPrefs = game.playerPrefs
    private val settingsPrefs = game.settingsPrefs
    private var gameView: GameView
    private var settingsView: SettingsView

    private val eWorld = world {
        injectables {
            add("gameStage", gameStage)
            add("uiStage", uiStage)
            add(textureAtlas)
            add(physichWorld)
            add(settingsPrefs)
        }

        components {
            add<ImageComponentListener>()
            add<PhysicComponentListener>()
            add<StateComponentListener>()
            add<AlertProjectileComponentListener>()
        }

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
        loadSkin()

        eWorld.systems.forEach { system ->
            if (system is EventListener) {
                gameStage.addListener(system)
                uiStage.addListener(system)
            }
        }

        if (!settingsPrefs.getBoolean("gyroscope")) {
            PlayerKeyBoardInput(eWorld, gameStage, uiStage)
        }

        uiStage.actors {
            gameView = gameView(GameModel(eWorld, uiStage), game.bundle, playerPrefs)
            settingsView = settingsView(bundle = game.bundle, prefs = settingsPrefs)
        }
        gameStage.addListener(this)
        uiStage.addListener(this)
    }

    override fun show() {
        uiStage.fire(GetCoinEvent(playerPrefs.getInteger("coins")))
        setMap("map/3.tmx")
    }

    private fun pauseWorld(pause: Boolean) {
        val mandatorySystems = setOf(
            AnimationSystem::class, CameraSystem::class, RenderSystem::class, DebugSystem::class
        )
        eWorld.systems.filter { it::class !in mandatorySystems }.forEach { it.enabled = !pause }
    }

    override fun pause() = pauseWorld(true) // TODO Do popup on exit the app

    override fun resume() {
        if (gameView.pausePopup.alpha != 1f || settingsView.alpha != 1f) {
            pauseWorld(false)
        }
    }

    private fun setMap(path: String) {
        currentMap?.disposeSafely()
        val newMap = TmxMapLoader().load(Gdx.files.internal(path).path())
        currentMap = newMap
        gameStage.fire(MapChangeEvent(newMap, PlayerComponent(coins = playerPrefs.getInteger("coins"))))
    }

    override fun render(delta: Float) {
        val dt = delta.coerceAtMost(0.25f)
        eWorld.update(dt)
    }

    override fun resize(width: Int, height: Int) {
        gameStage.viewport.update(width, height, true)
        uiStage.viewport.update(width, height, true)
    }

    override fun dispose() {
        eWorld.dispose()
        currentMap.disposeSafely()
        physichWorld.disposeSafely()
    }

    companion object {
        private val log = logger<GameScreen>()
    }

    override fun handle(event: Event): Boolean {
        when (event) {
            is GameResumeEvent -> {
                PlayerKeyBoardInput(eWorld, gameStage, uiStage)
                gameView.resume()
//                gameView.pausePopup.alpha = 0f
//                gameView.touchable = Touchable.disabled
//                gameView.pausePopup.touchable = Touchable.disabled
                resume()
            }

            is PausePopUpEvent -> {
                pause()
                Gdx.input.inputProcessor = uiStage
                gameView.pause()
//                gameView.pausePopup.alpha = 1f
//                gameView.touchable = Touchable.enabled
//                gameView.pausePopup.touchable = Touchable.enabled
            }

            is DeathHPopUpEvent -> {
                pause()
                Gdx.input.inputProcessor = uiStage
                gameView.death()
//                gameView.deathPopup.alpha = 1f
//                gameView.touchable = Touchable.enabled
//                gameView.deathPopup.touchable = Touchable.enabled
                if (event.playerCmp.meter > playerPrefs.getFloat("distance")) {
                    playerPrefs.putFloat("distance", event.playerCmp.meter)
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

            else -> return false
        }

        return true
    }

}