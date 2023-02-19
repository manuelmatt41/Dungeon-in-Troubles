package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.Qualifier
import ktx.assets.disposeSafely

/**
 * Sistema para representar las fisicas del juego y obtener diferentes datos del juego
 *
 * @property physicWorld Mundo de fisicas, se inicializa de forma automatica
 */
class DebugSystem(
    private val physicWorld: World,
    @Qualifier("gameStage") private val gameStage: Stage
) : IntervalSystem(enabled = false) {

    /**
     * Valor que se encarga de dibujar las fisicas en pantalla
     */
    private lateinit var physicsRenderer: Box2DDebugRenderer

    /**
     * Inicia el renderizadp si esta activado el sistema
     */
    init {
        if (enabled) {
            physicsRenderer = Box2DDebugRenderer()
        }
    }

    /**
     * Cada vez que se ejecuta el sistema pinta en pantalla la posicion actual de las fisicas del juego y pone en el titulos coloca diferentes datos
     */
    override fun onTick() {
        physicsRenderer.render(physicWorld, gameStage.camera.combined)

        Gdx.graphics.setTitle(
            buildString {
                append(Gdx.app.graphics.framesPerSecond)
                append("--")
                append(world.numEntities)
                append("--")
                append(physicWorld.contactCount)
            }
        )
    }

    /**
     * Libera recursos al cerrar el mundo de entidades
     */
    override fun onDispose() {
        if (enabled) {
            physicsRenderer.disposeSafely()
        }
    }
}