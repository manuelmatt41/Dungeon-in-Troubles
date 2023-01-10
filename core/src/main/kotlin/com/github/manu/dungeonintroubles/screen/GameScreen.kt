package com.github.manu.dungeonintroubles.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table.Debug
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.manu.dungeonintroubles.component.*
import com.github.manu.dungeonintroubles.component.ImageComponent.Companion.ImageComponentListener
import com.github.manu.dungeonintroubles.event.MapChangeEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.manu.dungeonintroubles.extension.physicCmpFromImage
import com.github.manu.dungeonintroubles.system.*
import com.github.quillraven.fleks.world
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.box2d.box
import ktx.box2d.createWorld
import ktx.log.logger
import ktx.math.vec2

class GameScreen : KtxScreen {
    private val gameStage: Stage = Stage(ExtendViewport(16f, 9f))
    private val textureAtlas: TextureAtlas = TextureAtlas(Gdx.files.internal("graphics/gameObjects.atlas"))
    private var currentMap: TiledMap? = null
    private val physichWorld = createWorld(vec2(0f, -10f)).apply {
        autoClearForces = false
    }

    private val eWorld = world {
        injectables {
            add("gameStage", gameStage)
            add(textureAtlas)
            add(physichWorld)
        }

        components {
            add<ImageComponentListener>()
        }

        systems {
            add<EntitySpawnSystem>()
            add<PhysicSystem>()
            add<AnimationSystem>()
            add<RenderSystem>()
            add<DebugSystem>()
        }
    }

    override fun show() {
        super.show()
        log.debug { "The game screen is shown" }

        eWorld.systems.forEach { system ->
            if (system is EventListener) {
                gameStage.addListener(system)
            }
        }

        currentMap = TmxMapLoader().load(Gdx.files.internal("map/map1.tmx").path())
        gameStage.fire(MapChangeEvent(currentMap!!))
    }

    override fun render(delta: Float) {
        val dt = delta.coerceAtMost(0.25f)
        eWorld.update(dt)
    }

    override fun resize(width: Int, height: Int) {
        gameStage.viewport.update(width, height, true)
    }

    override fun dispose() {
        gameStage.disposeSafely()
        eWorld.dispose()
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}