package com.github.manu.dungeonintroubles.extension

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.Stage

/**
 * Extrension de la clase Stage para lanzar eventos de forma mas legible
 *
 * @param event Tipo de evento que se va a lanzar
 */
fun Stage.fire(event: Event) {
    this.root.fire(event)
}