package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.DungeonInTroubles.Companion.UNIT_SCALE
import com.github.manu.dungeonintroubles.component.AlertProjectileComponent
import com.github.quillraven.fleks.*
import ktx.actors.alpha
import ktx.log.logger
import kotlin.math.log

@AllOf([AlertProjectileComponent::class])
class AlertProjectileSystem(
    @Qualifier("uiStage") private val uiStage: Stage,
    private val alertsCmps: ComponentMapper<AlertProjectileComponent>
) : IteratingSystem() {
    override fun onTickEntity(entity: Entity) {
        with(alertsCmps[entity]) {
            if (time >= lifeSpan) {
                world.remove(entity)
                return
            }
            time += deltaTime

            label.setPosition(position.x, position.y)

        }
    }

    companion object {
        private val log = logger<AlertProjectileSystem>()
    }
}