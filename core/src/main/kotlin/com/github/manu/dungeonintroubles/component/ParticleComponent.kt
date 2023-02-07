package com.github.manu.dungeonintroubles.component

import com.badlogic.gdx.graphics.g2d.ParticleEffect


enum class ParticleType {
    FLY, RUN, HIT, UNDEFINED;

    var atlasKey: String = this.toString().lowercase()
}

data class ParticleComponent(
    var particle: ParticleType = ParticleType.UNDEFINED,
) {
    var particleEffect: ParticleEffect? = null
}