package com.github.manu.dungeonintroubles.component

/**
 * Clase representa al movimiento de salto de la entidad
 */
data class JumpComponent(
    /**
     * Velocidad que tiene de salto la entidad, por defecto es 0
     */
    var speed: Float = 0f,
    /**
     * Seno de la entidad que indica la dirección del salto, por defecto es 0
     */
    var sin: Float = 0f,
)