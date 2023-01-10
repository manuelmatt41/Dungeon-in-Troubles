package com.github.manu.dungeonintroubles.extension

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.github.manu.dungeonintroubles.component.PhysicComponent
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.EntityCreateCfg
import ktx.box2d.BodyDefinition
import ktx.box2d.body

fun EntityCreateCfg.physicCmpFromImage(
    world: World,
    image: Image,
    bodyType: BodyType,
    fixtureAction: BodyDefinition.(PhysicComponent, Float, Float) -> Unit
) : PhysicComponent {
    val x = image.x
    val y = image.y
    val witdh = image.width
    val height = image.height

    return add {
        body = world.body(bodyType) {
            position.set(x + witdh * 0.5f, y + height * 0.5f)
            fixedRotation = true
            allowSleep = false
            this.fixtureAction(this@add, witdh, height)
        }
    }

}