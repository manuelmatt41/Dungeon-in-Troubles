package com.github.manu.dungeonintroubles.android

import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.github.manu.dungeonintroubles.DungeonInTroubles

/** Launches the Android application. */
class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize(DungeonInTroubles(), AndroidApplicationConfiguration().apply {
            // Configure your application here.
            useGyroscope = true
            useAccelerometer = true
        })
    }
}
