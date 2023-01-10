package com.github.manu.dungeonintroubles.extension

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.Stage

fun Stage.fire(event: Event) {
    this.root.fire(event)
}