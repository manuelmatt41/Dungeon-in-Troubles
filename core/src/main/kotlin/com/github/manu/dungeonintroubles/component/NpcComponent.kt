package com.github.manu.dungeonintroubles.component

/**
 * Componente que representa a los NPCs del sistema
 */
data class NpcComponent(
     /**
      * El tiempo que tarda la entidad en cambiar de dirección en el movimiento, por defecto es 1.5 segundos
      */
     var timeChangeDirection: Float = 1.5f
)