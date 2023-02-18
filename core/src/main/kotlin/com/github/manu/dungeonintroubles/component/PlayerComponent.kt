package com.github.manu.dungeonintroubles.component

/**
 * Componente que representa al jugador en el sistema
 */
data class PlayerComponent(
    /**
     * Monedas que tiene el jugador en partida, por defecto es 0
     */
    var coins: Int = 0,
    /**
     * Distancia quelleva el jugador en la partida, por defecto es 0
     */
    var distance: Float = 0f,
    /**
     * Velocidad en horinzotal que lleva el jugador en la partida, por defecto es 7.5 metros/segundo
     */
    var actualSpeed: Float = 7.5f
)