package com.github.manu.dungeonintroubles.ui.model

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.component.AnimationModel
import com.github.manu.dungeonintroubles.component.PlayerComponent
import com.github.manu.dungeonintroubles.event.GetCoinEvent
import com.github.manu.dungeonintroubles.event.MoveEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.manu.dungeonintroubles.system.PhysicSystem
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.World
import ktx.log.logger

/**
 * Modelo de la vista GameView que notifica de los cambios en las propiedades del jugador
 *
 *@param gameStage Escenario que representa el juego
 */
class GameModel(
    gameStage: Stage,
) : PropertyChangeSource(), EventListener {
    /**
     * Monedas del jugador que notifica del cambio cuando se establece un valor nuevo
     */
    var playerCoins by propertyNotify(0)
    /**
     * Distancia del jugador que notifica del cambio cuando se establece un valor nuevo
     */
    var playerDistance by propertyNotify(0f)

    /**
     * Al iniciar establece como escuchador de eventos esta misma clase
     */
    init {
        gameStage.addListener(this)
    }

    /**
     * Se ejecuta cuando se lanza un evento y comprueba si puede coger el evento y ejecutar codigo
     */
    override fun handle(event: Event): Boolean {
        return when (event) {
            is GetCoinEvent -> {
//                log.debug { "Get Coin" }
                playerCoins = event.coins
                true
            }

            is MoveEvent -> {
//                log.debug { "Move" }
                playerDistance = event.distance
                true
            }
            else -> false
        }
    }

    companion object {
        private val log = logger<GameModel>()
    }
}