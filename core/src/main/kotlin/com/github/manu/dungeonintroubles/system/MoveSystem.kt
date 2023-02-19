package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.component.*
import com.github.manu.dungeonintroubles.event.MoveEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.quillraven.fleks.*
import ktx.log.logger

/**
 * Sistema que se encarga del movimiento lateral de las entidades con MoveComponent
 *
 * @property uiStage Escenario que representa la interfaz del juego, se inicializa de forma automatica
 * @property moveCmps Conjunto de entidades con MoveComponent, se inicializa de forma automatica
 * @property physicsCmps Conjunto de entidades con PhysicComponent, se inicializa de forma automatica
 * @property playerCmps Conjunto de entidades con PlayerComponent, se inicializa de forma automatica
 * @property npcsCmps Conjunto de entidades con NpcComponent, se inicializa de forma automatica
 * @property imgCmps Conjunto de entidades con ImageComponent, se inicializa de forma automatica
 */
@AllOf([MoveComponent::class, PhysicComponent::class])
class MoveSystem(
    @Qualifier("uiStage") private val uiStage: Stage,
    private val moveCmps: ComponentMapper<MoveComponent>,
    private val physicsCmps: ComponentMapper<PhysicComponent>,
    private val playerCmps: ComponentMapper<PlayerComponent>,
    private val npcsCmps: ComponentMapper<NpcComponent>,
    private val imgCmps: ComponentMapper<ImageComponent>,
) : IteratingSystem() {

    /**
     * Distancia recorrida para ir incrementando la velocidad a lo largo de la partida
     */
    private var distanceToIncreaseSpeed: Float = 750f

    /**
     * Por cada entidad se calcula el impulso en el eje x
     *
     * @param entity Entidad a ejecutar
     *
     */
    override fun onTickEntity(entity: Entity) {
        val physcmp = physicsCmps[entity]
        val moveCmp = moveCmps[entity]
        val mass = physcmp.body.mass
        val velX = physcmp.body.linearVelocity.x

        physcmp.impulse.x = mass * (moveCmp.speed * moveCmp.cos - velX);

        // Se comprueba el tipo de entidad para calcula la distancia en caso del jugador y en los npc la direccion de movimiento fe forma aleatoria
        when (entity) {
            in playerCmps -> {
                with((playerCmps[entity])) {
                    distance += (moveCmp.speed * deltaTime) * 4f
                    uiStage.fire(MoveEvent(distance))

                    if (distance < LIMIT_SPEED && distance >= distanceToIncreaseSpeed) {
                        moveCmp.speed += 1f
                        distanceToIncreaseSpeed += 750f
                        log.debug { "${moveCmp.speed}" }
                    }

                    actualSpeed = moveCmp.speed
                }
            }

            in npcsCmps -> {
                with(npcsCmps[entity]) {
                    if (timeChangeDirection > 0f) {
                        timeChangeDirection -= deltaTime
                        return@with
                    }

                    moveCmp.cos = if (MathUtils.random(0, 1) == 0) -1f else 1f
                    timeChangeDirection = 2f
                }
            }
        }

        // Comprueba la dirrecion para girar la imagen o no
        imgCmps.getOrNull(entity)?.let { imageCmp ->
            if (moveCmp.cos != 0f) {
                imageCmp.image.flipX = moveCmp.cos < 0
            }
        }
    }

    companion object {
        private val log = logger<MoveSystem>()
        private const val LIMIT_SPEED = 5000f
    }
}