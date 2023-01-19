package com.github.manu.dungeonintroubles.event

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.scenes.scene2d.Event
import com.github.manu.dungeonintroubles.component.AnimationModel

class MapChangeEvent(val map: TiledMap, val trapMap: TiledMap) : Event()
class CollisionDespawnEvent(val cell: TiledMapTileLayer.Cell) : Event()
class GetCointEvent(val model: AnimationModel) : Event()
class TrapCollisionEvent(val model: AnimationModel) : Event()

class CrossPortalEvent() : Event() //TODO Search a portal model and animation
class DeathEvent() : Event() //TODO Update de class
