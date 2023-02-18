package com.github.manu.dungeonintroubles.component

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Shape2D
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import ktx.math.vec2

/**
 * Enumerado que representa al tipo de entidad que hay en el sistema
 */
enum class EntityType {
    PLAYER, DEMON, SLIME, SKELETON, TRAP, COIN, PORTAL, FIREBALL, SPAWNPOINT, UNDEFINED;
}

/**
 * Velocidad de movimiento por defecto que se establece a varias entidades
 */
const val DEFAULT_SPEED_X = 7.5f

/**
 * Velocidad de salto por defecto que se establece a varias entidades
 */
const val DEFAULT_SPEED_Y = 0.7f

/**
 * Clase con los datos para poder aparecer una entidad en el sistema
 */
data class SpawnConfiguration(
    /**
     * Modelo de imagen que usara la entidad
     */
    val model: AnimationModel,
    /**
     * Escalado de velocidad de la entidad, por defecto es 1
     */
    val speedScaling: Float = 1f,
    /**
     * Posicion del cuerpo de físicas en el sistema en relación a la posicion de la imagen, por defecto es 0
     */
    val physicOffset: Vector2 = vec2(0f, 0f),
    /**
     * Escalado del tamaño del cuerpo de físicas del sistema en la relación al tamaño de la imagen, por defecto es 0
     */
    val physicScaling: Vector2 = vec2(),
    /**
     * Tipo de cuerpo que tendra la entidad, por defecto es dinamico
     */
    val bodyType: BodyType = DynamicBody
)

/**
 * Componente que representa a la entidad que va a aparecer en el sistema
 */
data class SpawnComponent(
    /**
     * Tipo de de entidad que va a aparecer, por defecto es indefinido
     */
    var name: EntityType = EntityType.UNDEFINED,
    /**
     * Posicion donde va a aparecer en la pantalla, por defecto es 0
     */
    var location: Vector2 = vec2(),
    /**
     * Tamaño de la entidad en el sistema, por defecto es 0
     */
    var size: Vector2 = vec2(),
    /**
     * Forma que tiene la entidad, por defecto es restangular
     */
    var shape: Shape2D = Rectangle()
)