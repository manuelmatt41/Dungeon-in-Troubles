package com.github.manu.dungeonintroubles.event

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Event
import com.github.manu.dungeonintroubles.component.AnimationModel

class MapChangeEvent(val map: TiledMap) : Event()
class GetCointEvent(val model: AnimationModel) : Event()
class TrapCollisionEvent(val model: AnimationModel) : Event()
class CrossPortalEvent() : Event() //TODO Search a portal model and animation
class DeadEvent() : Event() //TODO Update de class
class SpawnTrapEvent(val layerName: String, val trapMap: TiledMap, val location: Vector2) : Event()