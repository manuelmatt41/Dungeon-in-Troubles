package com.github.manu.dungeonintroubles.event

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Event
import com.github.manu.dungeonintroubles.component.AnimationModel
import com.github.manu.dungeonintroubles.component.PlayerComponent

class MapChangeEvent(val map: TiledMap, val playerCmp: PlayerComponent = PlayerComponent()) : Event()
class GetCoinSoundEvent(val model: AnimationModel) : Event()
class TrapSoundCollisionEvent(val model: AnimationModel) : Event()
class CrossPortalSoundEvent() : Event() //TODO Search a portal model and animation
class DeadSoundEvent() : Event() //TODO Update de class
class SpawnObjectsEvent(val layerName: String, val map: TiledMap, val location: Vector2) : Event()