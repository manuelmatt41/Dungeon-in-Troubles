package com.github.manu.dungeonintroubles.ui.model

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.component.PlayerComponent
import com.github.manu.dungeonintroubles.event.GetCoinEvent
import com.github.manu.dungeonintroubles.event.MoveEvent
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.World
import ktx.log.logger

class GameModel(
    world: World,
    gameStage: Stage,
) : PropertyChangeSource(), EventListener {

    private val playerCmps: ComponentMapper<PlayerComponent> = world.mapper()

    var playerCoins by propertyNotify(0)
    var playerDistance by propertyNotify(0f)

    init {
        gameStage.addListener(this)
    }

    override fun handle(event: Event): Boolean {
        return when (event) {
            is GetCoinEvent -> {
                if (event.entity == null) {
                    log.debug { "Entity is null" }
                    false
                }
                if (event.entity!! !in playerCmps) {
                    log.debug { "Entity is not in playerCmps" }
                    false
                }

                log.debug { "Get Coin" }
                playerCoins = playerCmps[event.entity].coins
                true
            }

            is MoveEvent -> {
                if (event.entity == null) {
                    log.debug { "Entity is null" }
                    false
                }
                if (event.entity!! !in playerCmps) {
                    log.debug { "Entity is not in playerCmps" }
                    false
                }

                log.debug { "Move" }
                playerDistance = playerCmps[event.entity].meter
                true
            }
            else -> false
        }
    }

    companion object {
        private val log = logger<GameModel>()
    }
}