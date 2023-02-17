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