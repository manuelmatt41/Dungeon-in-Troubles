package com.github.manu.dungeonintroubles.event

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Event
import com.github.manu.dungeonintroubles.component.AnimationModel
import com.github.manu.dungeonintroubles.component.PlayerComponent
import com.github.quillraven.fleks.Entity

class MapChangeEvent(val map: TiledMap, val playerCmp: PlayerComponent = PlayerComponent()) : Event()
class GetCoinEvent(val coins: Int = 0) : Event()
class GetCoinSoundEvent(val model: AnimationModel) : Event()
class CrossPortalSoundEvent() : Event() //TODO Search a portal model and animation
class DeadSoundEvent() : Event() //TODO Update de class
class SpawnLayerObjectsEvent(val layerName: String, val map: TiledMap, val location: Vector2) : Event()
class SpawnProjectilesEvent(val numberOfProjectiles: Int = 0) : Event()
class MoveEvent(val model: AnimationModel, val distance: Float = 0f) : Event()
class GamePauseEvent() : Event()
class GameResumeEvent() : Event()
class SetGameEvent() : Event()
class SetMenuScreenEvent(val playerCmp: PlayerComponent? = null) : Event()
class ExitGameEvent() : Event()
class PausePopUpEvent(val playerCmp: PlayerComponent? = null) : Event()