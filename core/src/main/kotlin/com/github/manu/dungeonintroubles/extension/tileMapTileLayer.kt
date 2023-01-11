package com.github.manu.dungeonintroubles.extension

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell

fun TiledMapTileLayer.forEachCell(
    startX: Int,
    startY: Int,
    size: Int,
    action: (Cell, Int, Int) -> Unit
) {
    for (x in startX - size..startX + size) {
        for (y in startY - size..startY + size) {
            this.getCell(x, y)?.let { action(it, x, y) }
        }
    }
}