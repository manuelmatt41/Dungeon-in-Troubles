package com.github.manu.dungeonintroubles.component

import com.badlogic.gdx.graphics.g2d.ParticleEffect

/**
 * Enumarado que representa la particulas en distintos estados de la entidad
 */
enum class ParticleType {
    FLY, UNDEFINED;

    /**
     * Nombre de la imagen en el texture atlas
     */
    var atlasKey: String = this.toString().lowercase()
}

/**
 * Componente que representa a las particulas en el sistema
 */
data class ParticleComponent(
    /**
     * Tipo de particula que va a reproducir la entidad en el estado que se encuentre, por defecto es indefinido
     */
    var particle: ParticleType = ParticleType.UNDEFINED,
) {
    /**
     * Efecto de partcicula, por defecto es nul
     */
    var particleEffect: ParticleEffect? = null
}