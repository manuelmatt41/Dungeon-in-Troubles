package com.github.manu.dungeonintroubles.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.github.manu.dungeonintroubles.DungeonInTroubles
import com.github.manu.dungeonintroubles.component.ImageComponent
import com.github.manu.dungeonintroubles.component.ImageComponent.*
import com.github.manu.dungeonintroubles.component.ImageComponent.Companion.ImageComponentListener
import com.github.manu.dungeonintroubles.component.PhysicComponent
import com.github.manu.dungeonintroubles.component.PhysicComponent.Companion.PhysicComponentListener
import com.github.manu.dungeonintroubles.component.PlayerComponent
import com.github.manu.dungeonintroubles.event.ExitGameEvent
import com.github.manu.dungeonintroubles.event.MapChangeEvent
import com.github.manu.dungeonintroubles.event.SetGameEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.manu.dungeonintroubles.system.*
import com.github.manu.dungeonintroubles.ui.disposeSkin
import com.github.manu.dungeonintroubles.ui.loadSkin
import com.github.manu.dungeonintroubles.ui.view.MenuView
import com.github.manu.dungeonintroubles.ui.view.menuView
import com.github.quillraven.fleks.world
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import ktx.math.vec2
import ktx.scene2d.actors

class MenuScreen(val game: DungeonInTroubles) : KtxScreen, EventListener {
    private val uiStage = game.uiStage
    private val gameStage = game.gameStage
    private lateinit var menuView: MenuView
    private val prefs: Preferences = game.prefs
    private val textureAtlas = game.textureAtlas
    private var currentMap: TiledMap? = null
    private val physichWorld = createWorld(vec2(0f, -15f)).apply {
        autoClearForces = false
    }
    private val eWorld = world {
        injectables {
            add("uiStage",uiStage)
            add("gameStage", gameStage)
            add(textureAtlas)
            add(physichWorld)
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
        }
    }

    init {
        loadSkin()
        eWorld.systems.forEach { system ->
            if (system is EventListener) {
                gameStage.addListener(system)
            }
        }
        Gdx.input.inputProcessor = uiStage
        uiStage.addListener(this)
        uiStage.actors {
            menuView(bundle = game.bundle, stage = uiStage, prefs = game.prefs)
        }
    }

    override fun resize(width: Int, height: Int) {
        uiStage.viewport.update(width, height, true)
        gameStage.viewport.update(width, height, true)
    }

    private fun setMap(path: String) {
        currentMap?.disposeSafely()
        val newMap = TmxMapLoader().load(Gdx.files.internal(path).path())
        currentMap = newMap
        gameStage.fire(MapChangeEvent(newMap, PlayerComponent(coins = prefs.getInteger("coins"))))
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
        physichWorld.dispose()
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