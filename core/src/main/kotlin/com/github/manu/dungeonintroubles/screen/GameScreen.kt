package com.github.manu.dungeonintroubles.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.manu.dungeonintroubles.DungeonInTroubles
import com.github.manu.dungeonintroubles.component.ImageComponent.Companion.ImageComponentListener
import com.github.manu.dungeonintroubles.component.PhysicComponent.Companion.PhysicComponentListener
import com.github.manu.dungeonintroubles.component.StateComponent.Companion.StateComponentListener
import com.github.manu.dungeonintroubles.event.MapChangeEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.manu.dungeonintroubles.input.PlayerKeyBoardInput
import com.github.manu.dungeonintroubles.system.*
import com.github.manu.dungeonintroubles.system.GenerateMapSystem.Companion.NUMBER_OF_MAPS
import com.github.manu.dungeonintroubles.ui.disposeSkin
import com.github.manu.dungeonintroubles.ui.loadSkin
import com.github.manu.dungeonintroubles.ui.model.GameModel
import com.github.manu.dungeonintroubles.ui.view.gameView
import com.github.quillraven.fleks.world
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import ktx.log.logger
import ktx.math.vec2
import ktx.scene2d.actors

class GameScreen(game: DungeonInTroubles) : KtxScreen {
    private val gameStage: Stage = game.gameStage
    private val uiStage: Stage = game.uiStage
    private val textureAtlas = TextureAtlas(Gdx.files.internal("graphics/gameObjects.atlas"))
    private var currentMap: TiledMap? = null
    private val physichWorld = createWorld(vec2(0f, -15f)).apply {
        autoClearForces = false
    }

    private val eWorld = world {
        injectables {
            add("gameStage", gameStage)
            add("uiStage", uiStage)
            add(textureAtlas)
            add(physichWorld)
        }

        components {
            add<ImageComponentListener>()
            add<PhysicComponentListener>()
            add<StateComponentListener>()
        }

        systems {
            add<EntitySpawnSystem>()
            add<SpawnProjectilesSystem>()
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
            }
        }

        PlayerKeyBoardInput(eWorld, gameStage)

        uiStage.actors {
            gameView(GameModel(eWorld, gameStage))
        }
    }

    override fun show() {
        setMap("map/${MathUtils.random(1, NUMBER_OF_MAPS)}.tmx")
    }

    private fun pauseWorld(pause: Boolean) {
        val mandatorySystems = setOf(
            AnimationSystem::class,
            CameraSystem::class,
            RenderSystem::class,
            DebugSystem::class
        )
        eWorld.systems
            .filter { it::class !in mandatorySystems }
            .forEach { it.enabled = !pause }

    }

    override fun pause() = pauseWorld(true)

    override fun resume() = pauseWorld(false)

    private fun setMap(path: String) {
        currentMap?.disposeSafely()
        val newMap = TmxMapLoader().load(Gdx.files.internal(path).path())
        currentMap = newMap
        gameStage.fire(MapChangeEvent(newMap))
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
        gameStage.disposeSafely()
        uiStage.disposeSafely()
        textureAtlas.disposeSafely()
        eWorld.dispose()
        currentMap.disposeSafely()
        physichWorld.disposeSafely()
        disposeSkin()
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}