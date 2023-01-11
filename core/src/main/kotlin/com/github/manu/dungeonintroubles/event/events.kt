package com.github.manu.dungeonintroubles.event

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.scenes.scene2d.Event

class MapChangeEvent(val map: TiledMap) : Event()
class CollisionDespawnEvent(val cell: TiledMapTileLayer.Cell) : Event()