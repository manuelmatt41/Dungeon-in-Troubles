package com.github.manu.dungeonintroubles.event

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Event
import com.github.manu.dungeonintroubles.component.AnimationModel
import com.github.manu.dungeonintroubles.component.PlayerComponent

/**
 * Evento que se lanza al cambiar de mapa
 *
 * @property map Mapa que se va a cargar
 * @property playerCmp Componente que iniciar la entidad del jugador en el sistema
 *
 *  @constructor Crea un MapChangeEvent sin map
 */
class MapChangeEvent(val map: TiledMap, val playerCmp: PlayerComponent = PlayerComponent()) : Event()

/**
 * Evento que se lanza al coger una moneda
 *
 * @property coins Monedas que se han obtenido
 *
 * @constructor Crea un GetCoinEvent con valores por defecto
 */
class GetCoinEvent(val coins: Int = 0) : Event()

/**
 * Evento que se lanza cuando se coge una moneda para repoducir el sonido
 *
 * @property model Modelo de imagen la cual esta vinculado el sonido
 *
 * @constructor Crea un GetCoinSoundEvent vacio
 */
class GetCoinSoundEvent(val model: AnimationModel) : Event()

/**
 * Evento que se lanza cuando cruza un portal
 */
class CrossPortalSoundEvent : Event() //TODO Search a portal model and animation

/**
 * Evento que se lanza al morir el personaje
 */
class DeadSoundEvent : Event() //TODO Update de class

/**
 * Evento que se lanza para hacer aparacer objetos en el mapa
 *
 * @property layerName Nombre de la capa que contiene los objetos
 * @property map Mapa que contiene la capa que se va a cargar
 * @property location Posicion donde se va a colocar los objetos en el mapa
 *
 * @constructor Crea SpawnLayerObjectsEvent vacío
 */
class SpawnLayerObjectsEvent(val layerName: String, val map: TiledMap, val location: Vector2) : Event()

/**
 * Evento que se lanza al aparacer los proyectiles para reproducir sonido
 */
class SpawnProjectilesSoundEvent : Event()

/**
 * Evento que se lanza al moverse el personaje
 *
 * @property distance Distance recorrida por el jugador, por defecto es 0f
 *
 * @constructor Crea un MoveEvent con parametros por defecto
 */
class MoveEvent(val distance: Float = 0f) : Event()

/**
 * Evento que se lanza para volver a inciar la partida
 */
class GameResumeEvent : Event()

/**
 * Evento que se lanza para cambiar a la ventana de juego
 */
class SetGameEvent : Event()

/**
 * Evento que se lanza para cambiar a la ventana del menu principal
 */
class SetMenuScreenEvent : Event()

/**
 * Evento que se lanza para salir del juego
 */
class ExitGameEvent : Event()

/**
 * Evento que se lanza para lanzar el popup de pausa
 */
class PausePopUpEvent : Event()

/**
 * Evento que se lanza para crear el popup de perder la partida
 *
 * @property playerCmp Componente con la información del jugador para guardar los datos conseguidos
 *
 * @constructor Crea un DeadPopUpEvent vacio
 */
class DeadPopUpEvent(val playerCmp: PlayerComponent) : Event()

/**
 * Evento que se lanza para mostrar a la vista de ajustes
 */
class ShowSettingsEvent : Event()

/**
 * Evento que se lanza para esconder la vista de ajustes
 */
class HideSettingsEvent : Event()

/**
 * Evento que se lanza al aplicar los cambios en los ajustes
 */
class ChangeSettingsEvent : Event()