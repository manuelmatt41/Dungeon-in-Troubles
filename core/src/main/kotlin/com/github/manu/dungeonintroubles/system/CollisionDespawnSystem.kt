package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.component.TiledComponent
import com.github.manu.dungeonintroubles.event.CollisionDespawnEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.quillraven.fleks.*

@AllOf([TiledComponent::class])
class CollisionDespawnSystem(
    @Qualifier("gameStage") private val stage: Stage,
    private val tiledCmps: ComponentMapper<TiledComponent>,
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        with(tiledCmps[entity]) {
            if (nearbyEntities.isEmpty()) {
                stage.fire(CollisionDespawnEvent(cell))
                world.remove(entity)
            }
        }
    }
}