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

/**
 * Sistema que se encarga de dibujar las particulas de las entidades con ParticleComponent
 *
 * @property gameStage Escenario que representa el juego, se inicializa de forma automatica
 * @property particleCmps Conjunto de entidades con ParticleComponent, se inicializa de forma automatica
 * @property imgCmps Conjunto de entidades con ImageComponent, se inicializa de forma automatica
 * @property stateCmps Conjunto de entidades con StateComponent, se inicializa de forma automatica
 */
@AllOf([ParticleComponent::class])
class ParticleSystem(
    @Qualifier("gameStage") private val gameStage: Stage,
    private val particleCmps: ComponentMapper<ParticleComponent>,
    private val imgCmps: ComponentMapper<ImageComponent>,
    private val stateCmps: ComponentMapper<StateComponent>,
) : IteratingSystem() {
    /**
     * Mapa de las efectos de particulas ya creadas para ahorrar recursos
     */
    private val particleMap = mutableMapOf<String, ParticleEffect>()

    /**
     * Atlas de texturas de las diferentes particulas que se van a renderizar
     */
    private val particleAtlas = TextureAtlas(Gdx.files.internal("particle/particle.atlas"))

    /**
     * Por cada vez entidad se rendereza las particulas dependiendo del estado de la entidad
     *
     * @param entity Entidad a ejecutar
     *
     */
    override fun onTickEntity(entity: Entity) {
        val particleCmp = particleCmps[entity]
        val imgCmp = imgCmps[entity]
        // Crea el efecto de la partida y lo guarda
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
        //Posiciona el efecto
        particleCmp.particleEffect!!.setPosition(
            imgCmp.image.x + imgCmp.image.width * 0.5f,
            imgCmp.image.y
        )
        //Comprueba que este volando para pintar las particulas
        if (stateCmps[entity].stateMachine.currentState == PlayerState.FLY) {
            gameStage.batch.begin()
            particleCmp.particleEffect!!.draw(gameStage.batch, deltaTime)
            gameStage.batch.end()
        }

    }

    /**
     * Se ejecuta al liberar el mundo de entidades para liberear recursos
     */
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