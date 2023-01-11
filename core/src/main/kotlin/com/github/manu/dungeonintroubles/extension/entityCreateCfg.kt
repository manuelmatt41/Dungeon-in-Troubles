package com.github.manu.dungeonintroubles.extension

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Shape2D
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.github.manu.dungeonintroubles.DungeonInTroubles.Companion.UNIT_SCALE
import com.github.manu.dungeonintroubles.component.PhysicComponent
import com.github.manu.dungeonintroubles.component.PhysicComponent.Companion.TMP_VEC
import com.github.manu.dungeonintroubles.system.CollisionSpawnSystem.Companion.SPAWN_AREA_SIZE
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.EntityCreateCfg
import ktx.app.gdxError
import ktx.box2d.*
import ktx.math.vec2

fun EntityCreateCfg.physicCmpFromImage(
    world: World,
    image: Image,
    bodyType: BodyType,
    fixtureAction: BodyDefinition.(PhysicComponent, Float, Float) -> Unit
): PhysicComponent {
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

fun EntityCreateCfg.physicCmpFromShape2D(
    world: World,
    x: Int,
    y: Int,
    shape: Shape2D
): PhysicComponent {
    when (shape) {
        is Rectangle -> {
            val bodyX = x + shape.x * UNIT_SCALE
            val bodyY = y + shape.y * UNIT_SCALE
            val bodyW = shape.width * UNIT_SCALE
            val bodyH = shape.height * UNIT_SCALE

            return add {
                body = world.body(BodyType.StaticBody) {
                    position.set(bodyX, bodyY)
                    fixedRotation = true
                    allowSleep = false
                    loop(
                        vec2(0f, 0f),
                        vec2(bodyW, 0f),
                        vec2(bodyW, bodyH),
                        vec2(0f, bodyH)
                    ) {
                        friction = 0f
                    }

                    TMP_VEC.set(bodyW * 0.5f, bodyH * 0.5f)
                    box(SPAWN_AREA_SIZE + 3f, SPAWN_AREA_SIZE + 3f, TMP_VEC) {
                        isSensor = true
                    }
                }
            }
        }

        else -> gdxError("Shape $shape is not supported")
    }
}