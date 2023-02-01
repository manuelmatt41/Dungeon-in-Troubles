package com.github.manu.dungeonintroubles.component

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

enum class AnimationModel {
    PLAYER, DEMON, SLIME, SKELETON, TRAP, COIN, PORTAL, FIREBALL, NONE,UNDEFINED;

    var atlasKey: String = this.toString().lowercase()
}

enum class AnimationType {
    IDLE, RUN, HIT;

    var atlasKey: String = this.toString().lowercase()
}

data class AnimationComponent(
    var model: AnimationModel = AnimationModel.UNDEFINED,
    var stateTime: Float = 0f,
    var playMode: PlayMode = PlayMode.LOOP,
) {
    lateinit var animation: Animation<TextureRegionDrawable>
    var nextAnimation: String = NO_ANIMATION

    val isAnimationDone: Boolean
        get() = animation.isAnimationFinished(stateTime)

    fun nextAnimation(model: AnimationModel, type: AnimationType) {
        this.model = model
        nextAnimation = "${model.atlasKey}/${type.atlasKey}"
    }

    fun nextAnimation(type: AnimationType) {
        nextAnimation = "${model.atlasKey}/${type.atlasKey}"
    }

    companion object {
        const val NO_ANIMATION = ""
    }
}