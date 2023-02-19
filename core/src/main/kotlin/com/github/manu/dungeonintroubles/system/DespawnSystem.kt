package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.component.*
import com.github.manu.dungeonintroubles.event.DeadPopUpEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.quillraven.fleks.*
import ktx.log.logger

/**
 * Sistema que se encarga de hacer desaparecer las entidades con el DespawnComponent del mundo
 *
 * @property gameStage Escenario que representa el juego
 * @property despawnCmps Conjunto de entidades que tienen DespawnComponent, se incializa de forma automactica
 * @property despawnCmps Conjunto de entidades que tienen DespawnComponent, se incializa de forma automactica
 * @property playerCmps Conjunto de entidades que tienen PlayerComponent, se incializa de forma automactica
 * @property imgCmps Conjunto de entidades que tienen ImageComponent, se incializa de forma automactica
 */
@AnyOf([DespawnComponent::class, TrapComponent::class, CoinComponent::class, PlayerComponent::class])
@NoneOf([SpawnComponent::class])
class DespawnSystem(
    @Qualifier("gameStage") private val gameStage: Stage,
    private val despawnCmps: ComponentMapper<DespawnComponent>,
    private val playerCmps: ComponentMapper<PlayerComponent>,
    private val imgCmps: ComponentMapper<ImageComponent>,
) : IteratingSystem() {
    /**
     * Posicion del jugador para hacer desaparecer las entidades al pasar este mismo
     */
    private var locationX: Float = 0f;

    /**
     * Por cada entidad en el sistema lo hace desaparecer ya sea que queda atras del jugador o que este mismo pierde y lanza un evento
     *
     * @param entity Entidad a ejecutar
     *
     */
    override fun onTickEntity(entity: Entity) {
        if (entity in despawnCmps) {
            if (entity in playerCmps) {
                gameStage.fire(DeadPopUpEvent(playerCmps[entity]))
            }

            world.remove(entity)
            return
        }

        if (entity in playerCmps) {
            locationX = imgCmps[entity].image.x - gameStage.camera.viewportWidth
            return
        }

        with(imgCmps[entity]) {
            if (this.image.x < locationX) {
                configureEntity(entity) {
                    despawnCmps.add(it)
                }
            }
        }
    }

    companion object {
        private val log = logger<DespawnSystem>()
    }
}