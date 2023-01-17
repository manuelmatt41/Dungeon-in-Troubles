package com.github.manu.dungeonintroubles.system

import com.github.manu.dungeonintroubles.component.JumpComponent
import com.github.manu.dungeonintroubles.component.MoveComponent
import com.github.manu.dungeonintroubles.component.PhysicComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2

@AllOf([JumpComponent::class, PhysicComponent::class])
class JumpMoveSystem(
    private val physicsCmps: ComponentMapper<PhysicComponent>,
    private val jumpCmps: ComponentMapper<JumpComponent>,
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        val physcmp = physicsCmps[entity]
        val jumpCmp = jumpCmps[entity]

        physcmp.impulse.y = if (jumpCmp.sin != 0f) jumpCmp.speed else 0f
    }

    companion object {
        private val log = logger<JumpMoveSystem>()
    }
}