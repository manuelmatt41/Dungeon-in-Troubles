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
import com.github.manu.dungeonintroubles.ui.view.MenuView
import com.github.manu.dungeonintroubles.ui.view.SettingsView
import com.github.manu.dungeonintroubles.ui.view.menuView
import com.github.manu.dungeonintroubles.ui.view.settingsView
import com.github.quillraven.fleks.world
import ktx.actors.alpha
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import ktx.math.vec2
import ktx.scene2d.actors
import ktx.tiled.height

class MenuScreen(val game: DungeonInTroubles) : KtxScreen, EventListener {
    private val uiStage = game.uiStage
    private val gameStage = game.gameStage
    private lateinit var menuView: MenuView
    private lateinit var settingsView: SettingsView
    private val playerPrefs: Preferences = game.playerPrefs
    private val settingsPrefs: Preferences = game.settingsPrefs
    private val textureAtlas = game.textureAtlas
    private var currentMap: TiledMap? = null
    private val physichWorld = createWorld(vec2(0f, -15f)).apply {
        autoClearForces = false
    }
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
            menuView = menuView(bundle = game.bundle, prefs = game.playerPrefs)
            settingsView = settingsView(game.bundle, game.settingsPrefs)
        }

        uiStage.addListener(this)
    }

    override fun resize(width: Int, height: Int) {
        uiStage.viewport.update(width, height, true)
        gameStage.viewport.update(width, height, true)
    }

    private fun setMap(path: String) {
        currentMap?.disposeSafely()
        val newMap = TmxMapLoader().load(Gdx.files.internal(path).path())
        currentMap = newMap
        gameStage.camera.position.y = currentMap!!.height * 0.5f
        gameStage.fire(MapChangeEvent(newMap, PlayerComponent(coins = playerPrefs.getInteger("coins"))))
    }

    override fun show() {
        setMap("map/3.tmx")
    }

    override fun render(delta: Float) {
        val dt = delta.coerceAtMost(0.25f)
        eWorld.update(dt)
        uiStage.act()
        uiStage.draw()
    }


    override fun dispose() {
        super.dispose()
        eWorld.dispose()
        currentMap?.disposeSafely()
        physichWorld.disposeSafely()
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

            is ShowSettingsEvent -> {
                menuView.touchable = Touchable.disabled
                menuView.alpha = 0f

                settingsView.alpha = 1f
                settingsView.touchable = Touchable.enabled
            }

            is HideSettingsEvent -> {
                menuView.touchable = Touchable.enabled
                menuView.alpha = 1f

                settingsView.alpha =0f
                settingsView.touchable = Touchable.disabled
            }

            is ExitGameEvent -> {
                playerPrefs.flush()
                Gdx.app.exit()
            }

            else -> return false
        }
        return true;
    }
}