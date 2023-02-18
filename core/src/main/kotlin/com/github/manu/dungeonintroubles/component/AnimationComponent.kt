package com.github.manu.dungeonintroubles.component

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

/**
 * Enumerado que representa al modelo de imagen
 */
enum class AnimationModel {
    PLAYER, DEMON, SLIME, SKELETON, TRAP, COIN, PORTAL, FIREBALL, NONE, UNDEFINED;

    /**
     * Nombre que tiene el modelo en el texture atlas
     */
    var atlasKey: String = this.toString().lowercase()
}

/**
 * Tipo de animacion que se ejecuta en el sistema
 */
enum class AnimationType {
    RUN, HIT;

    /**
     * Nombre que tiene la animación en el  texture atlas
     */
    var atlasKey: String = this.toString().lowercase()
}

/**
 * Componente que es la animacion de la entidad
 *
 * @property model Modelo de la entidad que sera la imagen que lo representa, por defecto esta indefinido
 * @property stateTime Tiempo que hay entre frames de la animación, por defecto es 0f
 * @property playMode EL modo que se va a reproducir la aniamción, por defecto está en LOOP
 *
 * @constructor Crea un AnimationComponent con valores por defecto
 */
data class AnimationComponent(
    var model: AnimationModel = AnimationModel.UNDEFINED,
    var stateTime: Float = 0f,
    var playMode: PlayMode = PlayMode.LOOP,
) {
    /**
     * Variable que se inicializa en el sistema de aniamcion que representa a la animación que se va a representar usando un texture atlas
     */
    lateinit var animation: Animation<TextureRegionDrawable>

    /**
     * Animación a la cual se va a cambiar, por defecto esta en NO_ANIMATION
     */
    var nextAnimation: String = NO_ANIMATION

    /**
     * Cambia la aniamcion actual por otra
     *
     * @param model Modelo de imagen que se va usar para representar la entidad
     * @param type El tipo  de animacion que se va a usar
     */
    fun nextAnimation(model: AnimationModel, type: AnimationType) {
        this.model = model
        nextAnimation = "${model.atlasKey}/${type.atlasKey}"
    }

    /**
     * Cambia la aniamcion actual por otra
     *
     * @param type El tipo  de animacion que se va a usar
     */
    fun nextAnimation(type: AnimationType) {
        nextAnimation = "${model.atlasKey}/${type.atlasKey}"
    }

    companion object {
        /**
         * Conteste que representa que no hay animacion para cambiar en el sistema
         */
        const val NO_ANIMATION = ""
    }
}