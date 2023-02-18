package com.github.manu.dungeonintroubles.component

/**
 * Componente de movimiento horizontal de la entidad
 */
class MoveComponent(
    /**
     * Velocidad de la entidad en horizontal, por defecto es 0
     */
    var speed: Float = 0f,
    /**
     * Coseno que representa la dirección del movimiento en el eje X, por defecto es 0
     */
    var cos: Float = 0f,
)