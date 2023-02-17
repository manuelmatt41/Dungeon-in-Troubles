package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.component.*
import com.github.manu.dungeonintroubles.event.DeathHPopUpEvent
import com.github.manu.dungeonintroubles.event.PausePopUpEvent
import com.github.manu.dungeonintroubles.event.SetMenuScreenEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.quillraven.fleks.*
import ktx.log.debug
import ktx.log.logger
import ktx.math.vec2

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
                gameStage.fire(DeathHPopUpEvent(playerCmps[entity]))
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