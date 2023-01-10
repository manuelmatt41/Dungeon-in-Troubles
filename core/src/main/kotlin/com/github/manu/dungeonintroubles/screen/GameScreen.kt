package com.github.manu.dungeonintroubles.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.quillraven.fleks.world
import ktx.app.KtxScreen
import ktx.log.logger

class GameScreen : KtxScreen {
    private val gameStage: Stage = Stage(ExtendViewport(16f, 9f))
    private val textureAtlas: TextureAtlas = TextureAtlas(Gdx.files.internal("graphics/gameObjects.atlas"))
    private var currentMap: TiledMap? = null;

    private val eWorld = world {
        injectables {
            add("gameStage", gameStage)
            add(textureAtlas)
        }
    }

    override fun show() {
        super.show()
        log.debug { "The game screen is shown" }

        currentMap = TmxMapLoader().load(Gdx.files.internal("map/map1.tmx").path())
        gameStage.fir
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}