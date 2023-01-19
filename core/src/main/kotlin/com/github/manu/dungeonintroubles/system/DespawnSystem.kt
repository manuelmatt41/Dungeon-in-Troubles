package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.component.DespawnComponent
import com.github.manu.dungeonintroubles.component.PlayerComponent
import com.github.manu.dungeonintroubles.event.MapChangeEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.quillraven.fleks.*

@AllOf([DespawnComponent::class])
class DespawnSystem : IteratingSystem() {
    override fun onTickEntity(entity: Entity) {
        world.remove(entity)
    }
}