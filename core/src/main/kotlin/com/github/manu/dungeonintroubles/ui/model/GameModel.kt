package com.github.manu.dungeonintroubles.ui.model

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.World

class GameModel(
    world: World,
    stage: Stage,
) : PropertyChangeSource(), EventListener {



    override fun handle(event: Event): Boolean {

        return true
    }
}