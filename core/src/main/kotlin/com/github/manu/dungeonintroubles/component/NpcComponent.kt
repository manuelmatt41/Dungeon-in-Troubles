package com.github.manu.dungeonintroubles.component

/**
 * Componente que representa a los NPCs del sistema
 *
 * @property timeChangeDirection El tiempo que tarda la entidad en cambiar de dirección en el movimiento, por defecto es 1.5 segundos
 *
 * @constructor Crea un NpcComponent con valores por defecto
 */
data class NpcComponent(
     var timeChangeDirection: Float = 1.5f
)