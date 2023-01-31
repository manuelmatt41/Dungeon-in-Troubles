package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.github.manu.dungeonintroubles.component.AnimationComponent
import com.github.manu.dungeonintroubles.component.AnimationComponent.Companion.NO_ANIMATION
import com.github.manu.dungeonintroubles.component.AnimationModel
import com.github.manu.dungeonintroubles.component.ImageComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.app.gdxError
import ktx.collections.map
import ktx.log.logger

@AllOf([AnimationComponent::class, ImageComponent::class])
class AnimationSystem(
    private val textureAtlas: TextureAtlas,
    private val animCmps: ComponentMapper<AnimationComponent>,
    private val imgCmps: ComponentMapper<ImageComponent>,
) : IteratingSystem() {

    private val cachedAnimations = mutableMapOf<String, Animation<TextureRegionDrawable>>()

    override fun onTickEntity(entity: Entity) {
        val animCmp = animCmps[entity]

        if (animCmp.nextAnimation == NO_ANIMATION) {
            animCmp.stateTime += deltaTime
        } else {
            animCmp.animation = animation(animCmp.nextAnimation, animCmp.model)
            animCmp.stateTime = 0f
            animCmp.nextAnimation = NO_ANIMATION
        }

        animCmp.animation.playMode = animCmp.playMode
        imgCmps[entity].image.drawable = animCmp.animation.getKeyFrame(animCmp.stateTime)
    }

    private fun animation(animationKeyPath: String, model: AnimationModel): Animation<TextureRegionDrawable> {
        return cachedAnimations.getOrPut(animationKeyPath) {
            log.debug { "New animation is created for $animationKeyPath" }
            val regions = textureAtlas.findRegions(animationKeyPath)

            if (regions.isEmpty) {
                gdxError("There are no texture regions for $animationKeyPath")
            }

            Animation(if (model != AnimationModel.FIREBALL) DEFAULT_FRAME_DURATION else 1/ 20f, regions.map { TextureRegionDrawable(it) })
        }
    }

    companion object {
        private val log = logger<AnimationSystem>()
        private const val DEFAULT_FRAME_DURATION = 1 / 12f
    }
}