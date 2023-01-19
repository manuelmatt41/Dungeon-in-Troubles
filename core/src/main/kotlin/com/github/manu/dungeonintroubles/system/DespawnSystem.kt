package com.github.manu.dungeonintroubles.system

import com.github.manu.dungeonintroubles.component.DespawnComponent
import com.github.quillraven.fleks.*

@AllOf([DespawnComponent::class])
class DespawnSystem : IteratingSystem() {
    override fun onTickEntity(entity: Entity) {
        world.remove(entity)
    }
}