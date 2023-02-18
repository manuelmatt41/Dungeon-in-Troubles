package com.github.manu.dungeonintroubles.component

/**
 * Clase representa al movimiento de salto de la entidad
 *
 * @property speed Velocidad que tiene de salto la entidad, por defecto es 0
 * @property sin Seno de la entidad que indica la dirección del salto, por defecto es 0
 *
 * @constructor Crea un JumpComponent con valores por defecto
 */
data class JumpComponent(
    var speed: Float = 0f,
    var sin: Float = 0f,
)