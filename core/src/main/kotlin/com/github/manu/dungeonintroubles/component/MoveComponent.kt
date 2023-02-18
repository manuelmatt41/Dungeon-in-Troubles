package com.github.manu.dungeonintroubles.component

/**
 * Componente de movimiento horizontal de la entidad
 *
 * @property speed  Velocidad de la entidad en horizontal, por defecto es 0
 * @property cos Coseno que representa la dirección del movimiento en el eje X, por defecto es 0
 *
 * @constructor Crea un MoveComponent con valores por defecto
 */
class MoveComponent(
    var speed: Float = 0f,
    var cos: Float = 0f,
)