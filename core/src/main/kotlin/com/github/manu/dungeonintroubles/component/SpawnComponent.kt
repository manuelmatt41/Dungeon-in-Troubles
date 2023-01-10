package com.github.manu.dungeonintroubles.component

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import ktx.math.vec2

data class SpawnConfiguration(
    val model: AnimationModel,
    val speedScaling: Float = 1f,
    val physicOffset: Vector2 = vec2(0f, 0f),
    val physicScaling: Vector2 = vec2(),
    val bodyType: BodyType = DynamicBody
)

data class SpawnComponent(
    var type: String = "",
    var location: Vector2 = vec2()
) {
}