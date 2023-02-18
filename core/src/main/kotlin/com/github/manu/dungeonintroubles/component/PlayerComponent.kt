package com.github.manu.dungeonintroubles.component

/**
 * Componente que representa al jugador en el sistema
 *
 * @property coins Monedas que tiene el jugador en partida, por defecto es 0
 * @property distance Distancia quelleva el jugador en la partida, por defecto es 0
 * @property actualSpeed Velocidad en horinzotal que lleva el jugador en la partida, por defecto es 7.5 metros/segundo
 *
 * @constructor Crea un PlayerComponent con valores por defecto
 */
data class PlayerComponent(
    var coins: Int = 0,
    var distance: Float = 0f,
    var actualSpeed: Float = 7.5f
)