package com.github.manu.dungeonintroubles.event

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.scenes.scene2d.Event

class MapChangeEvent(val map: TiledMap) : Event()