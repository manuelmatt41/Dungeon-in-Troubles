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

/**
 * Sistema de animaciones encargado de mostrar la animacion por pantalla a todas las entidades con la AnimationComponent
 *
 * @property textureAtlas Atlas con las animacion que se van cargar, se incializa de forma automatica
 * @property animCmps Conjunto de entidades con AnimationComponent, se inicializa de forma automatica
 * @property imgCmps Conjunto de entidades con ImageComponent, se inicializa de forma automatica
 */
@AllOf([AnimationComponent::class, ImageComponent::class])
class AnimationSystem(
    private val textureAtlas: TextureAtlas,
    private val animCmps: ComponentMapper<AnimationComponent>,
    private val imgCmps: ComponentMapper<ImageComponent>,
) : IteratingSystem() {
    /**
     * Mapa de animaciones que estan cargadas para ahorrar tiempo de ejecucion
     */
    private val cachedAnimations = mutableMapOf<String, Animation<TextureRegionDrawable>>()

    /**
     * Por cada entidad en el sistema actualiza la aniamcion o cambia la animacion que corresponda
     *
     * @param entity Entidad a ejecutar
     */
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

    /**
     * Crea una animacion a traves del texture atlas y se guarda en el cache
     *
     * @param animationKeyPath Nombre de la animacin en el atlas
     * @param model NOmbre del modelo que contienela animacion
     *
     * @return Devuelve una aniamcion cargada a traves del texture atlas
     */
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

        /**
         * Valor constante que define el tiempo entre frames de aniamcion
         */
        private const val DEFAULT_FRAME_DURATION = 1 / 12f
    }
}