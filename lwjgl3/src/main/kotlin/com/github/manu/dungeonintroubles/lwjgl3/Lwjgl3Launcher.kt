@file:JvmName("Lwjgl3Launcher")

package com.github.manu.dungeonintroubles.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.github.manu.dungeonintroubles.DungeonInTroubles

/** Launches the desktop (LWJGL3) application. */
fun main() {
    Lwjgl3Application(DungeonInTroubles(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("dungeonintroubles")
//        useVsync(false)
        setWindowedMode(640, 480)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
