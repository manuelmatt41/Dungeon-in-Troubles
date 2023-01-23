package com.github.manu.dungeonintroubles.component

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Shape2D
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import ktx.math.vec2

enum class EntityType {
    PLAYER, TRAP, COIN, SPAWNPOINT, UNDEFINED;

    var type: String = this.toString().lowercase()
}

const val DEFAULT_SPEED_X = 10f
const val DEFAULT_SPEED_Y = 0.7f

data class SpawnConfiguration(
    val model: AnimationModel,
    val speedScaling: Float = 1f,
    val physicOffset: Vector2 = vec2(0f, 0f),
    val physicScaling: Vector2 = vec2(),
    val bodyType: BodyType = DynamicBody
)

data class SpawnComponent(
    var name: EntityType = EntityType.UNDEFINED,
    var location: Vector2 = vec2(),
    var size: Vector2 = vec2(),
    var shape: Shape2D = Rectangle()
) {
}