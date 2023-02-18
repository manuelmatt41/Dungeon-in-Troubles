package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.component.*
import com.github.manu.dungeonintroubles.event.DeadPopUpEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.quillraven.fleks.*
import ktx.log.logger

@AnyOf([DespawnComponent::class, TrapComponent::class, CoinComponent::class, PlayerComponent::class])
@NoneOf([SpawnComponent::class])
class DespawnSystem(
    @Qualifier("gameStage") private val gameStage: Stage,
    private val despawnCmps: ComponentMapper<DespawnComponent>,
    private val playerCmps: ComponentMapper<PlayerComponent>,
    private val imgCmps: ComponentMapper<ImageComponent>,
) : IteratingSystem() {
    private var locationX: Float = 0f;
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