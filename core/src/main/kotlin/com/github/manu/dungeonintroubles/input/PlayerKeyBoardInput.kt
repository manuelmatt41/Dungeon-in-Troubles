package com.github.manu.dungeonintroubles.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys.P
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.component.JumpComponent
import com.github.manu.dungeonintroubles.component.PlayerComponent
import com.github.manu.dungeonintroubles.component.SpawnComponent
import com.github.manu.dungeonintroubles.event.GameResumeEvent
import com.github.manu.dungeonintroubles.event.PausePopUpEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.World
import ktx.app.KtxInputAdapter
import ktx.log.logger

/**
 * Clase encargada de recibir las entradas de los controles del juego
 *
 * @param world Mundo que representa el conjuntos de entidades
 * @property  gameStage Escenario donde se representa el juego
 * @param uiStage Escenario donde se representa la ui del juego
 * @property jumpCmps Entidades con JumpComponent que se inicializa con un mapero del mundo
 *
 * @constructor Crea un PlayerKeyBoardInput vacio
 */
class PlayerKeyBoardInput(
    world: World,
    private val gameStage: Stage,
    uiStage: Stage,
    private val jumpCmps: ComponentMapper<JumpComponent> = world.mapper(),
) : KtxInputAdapter {

    /**
     * Direccion en e eje Y del jugador, por defecto 0f
     */
    private var playerSin = 0f

    /**
     * Entidades con PlayerComponent
     */
    private val playerEntities =
        world.family(allOf = arrayOf(PlayerComponent::class), noneOf = arrayOf(SpawnComponent::class))

    /**
     * Al iniciar la clase se establece como clase principal que escucha las entradas de los controles junto al escenario que representa la UI
     */
    init {
        val multiplexer = InputMultiplexer()
        multiplexer.addProcessor(uiStage)
        multiplexer.addProcessor(this)

        Gdx.input.inputProcessor = multiplexer
    }

    /**
     * Actualiza los valores del JumpComponent de los jugadores
     */
    private fun updatePlayerMovement() {
        playerEntities.forEach { player ->
            with(jumpCmps[player]) {
                sin = playerSin
            }
        }
    }

    /**
     * Se ejecuta al pulsar o clickar en la pantalla
     *
     * @param screenX Posicion X donde se pulso
     * @param screenY Posicion Y donde se pulso
     * @param pointer
     * @param button Boton del ratón que se ha pulsado
     *
     * @return Devuelve true si se ha ejecutado la función
     */
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        //Actualiza la direccionde salto del jugador
        playerSin = 1f
        updatePlayerMovement()
        return true
    }

    /**
     * Se ejecuta al dejar pulsar o clickar en la pantalla
     *
     * @param screenX Posicion X donde se pulso
     * @param screenY Posicion Y donde se pulso
     * @param pointer
     * @param button Boton del ratón que se ha pulsado
     *
     * @return Devuelve true si se ha ejecutado la función
     */
    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        //Actualiza la direccionde salto del jugador
        playerSin = 0f
        updatePlayerMovement()
        return true
    }

    /**
     * Se ejecuta al pulsar el teclado
     *
     * @param Codigo de la tecla que se ha pulsado
     *
     * @return Devuelve true si se ha ejecutado correctamente la funcion
     */
    override fun keyDown(keycode: Int): Boolean {
        //COmprueba la letra P y lanza el evento para pausar el juego
        if (keycode == P) {
            gameStage.fire(PausePopUpEvent())
        }
        return true;
    }
}