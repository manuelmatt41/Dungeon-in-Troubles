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

@AllOf([ParticleComponent::class])
class ParticleSystem(
    @Qualifier("gameStage") private val gameStage: Stage,
    private val textureAtlas: TextureAtlas,
    private val particleCmps: ComponentMapper<ParticleComponent>,
    private val imgCmps: ComponentMapper<ImageComponent>,
    private val stateCmps: ComponentMapper<StateComponent>,
) : IteratingSystem() {
    private val particleMap = mutableMapOf<String, ParticleEffect>()

    override fun onTickEntity(entity: Entity) {
        val particleCmp = particleCmps[entity]
        val imgCmp = imgCmps[entity]

//        if (particleCmp.particleEffect == null) {
//            particleCmp.particleEffect = particleMap.getOrPut("particle/${particleCmp.particle.atlasKey}"){
//                val particleEffect = ParticleEffect();
//                particleEffect.load(
//                    Gdx.files.internal("particle/${particleCmp.particle.atlasKey}"),
//                    textureAtlas
//                )
//                particleEffect.scaleEffect(0.05f)
//                particleEffect.flipY()
//                particleEffect
//            }
//        }

        particleCmp.particleEffect = particleMap.getOrPut("particle/${particleCmp.particle.atlasKey}") {
            val particleEffect = ParticleEffect();
            particleEffect.load(
                Gdx.files.internal("particle/${particleCmp.particle.atlasKey}"),
                textureAtlas
            )
            particleEffect.scaleEffect(0.05f)
            particleEffect.flipY()
            particleEffect
        }

        particleCmp.particleEffect!!.setPosition(imgCmp.image.x + imgCmp.image.width * 0.5f, imgCmp.image.y)

        if (stateCmps[entity].stateMachine.currentState == PlayerState.FLY) {
            gameStage.batch.begin()
            particleCmp.particleEffect!!.draw(gameStage.batch, deltaTime)
            gameStage.batch.end()
        }

    }
}