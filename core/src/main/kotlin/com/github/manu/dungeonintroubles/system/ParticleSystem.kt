package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.manu.dungeonintroubles.DungeonInTroubles.Companion.UNIT_SCALE
import com.github.manu.dungeonintroubles.ai.PlayerState
import com.github.manu.dungeonintroubles.component.ImageComponent
import com.github.manu.dungeonintroubles.component.ParticleComponent
import com.github.manu.dungeonintroubles.component.StateComponent
import com.github.quillraven.fleks.*
import ktx.assets.disposeSafely
import ktx.log.logger

@AllOf([ParticleComponent::class])
class ParticleSystem(
    @Qualifier("gameStage") private val gameStage: Stage,
    private val particleCmps: ComponentMapper<ParticleComponent>,
    private val imgCmps: ComponentMapper<ImageComponent>,
    private val stateCmps: ComponentMapper<StateComponent>,
) : IteratingSystem() {

    private val particleMap = mutableMapOf<String, ParticleEffect>()
    private val particleAtlas = TextureAtlas(Gdx.files.internal("particle/particle.atlas"))

    override fun onTickEntity(entity: Entity) {
        val particleCmp = particleCmps[entity]
        val imgCmp = imgCmps[entity]

        particleCmp.particleEffect = particleMap.getOrPut("particle/${particleCmp.particle.atlasKey}") {
            val particleEffect = ParticleEffect();
            log.debug { "particle/${particleCmp.particle.atlasKey}" }
            particleEffect.load(
                Gdx.files.internal("particle/${particleCmp.particle.atlasKey}"),
                particleAtlas
            )
            particleEffect.scaleEffect(0.075f)
            particleEffect
        }

        particleCmp.particleEffect!!.setPosition(
            imgCmp.image.x + imgCmp.image.width * 0.5f,
            imgCmp.image.y
        )

        if (stateCmps[entity].stateMachine.currentState == PlayerState.FLY) {
            gameStage.batch.begin()
            particleCmp.particleEffect!!.draw(gameStage.batch, deltaTime)
            gameStage.batch.end()
        }

    }

    override fun onDispose() {
        particleAtlas.disposeSafely()
        particleMap.forEach {
            it.value.disposeSafely()
        }
    }

    companion object {
        private val log = logger<ParticleSystem>()
    }
}