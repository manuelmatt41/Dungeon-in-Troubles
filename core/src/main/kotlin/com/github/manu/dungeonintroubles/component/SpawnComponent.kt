package com.github.manu.dungeonintroubles.component

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import ktx.math.vec2

const val DEFAULT_SPEED_X = 7.5f
const val DEFAULT_SPEED_Y = 0.7f
data class SpawnConfiguration(
    val model: AnimationModel,
    val speedScaling: Float = 1f,
    val physicOffset: Vector2 = vec2(0f, 0f),
    val physicScaling: Vector2 = vec2(),
    val bodyType: BodyType = DynamicBody
)

data class SpawnComponent(
    var name: String = "",
    var location: Vector2 = vec2(),
) {
}