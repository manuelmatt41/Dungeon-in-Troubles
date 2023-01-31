package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.component.PlayerComponent
import com.github.manu.dungeonintroubles.event.MapChangeEvent
import com.github.manu.dungeonintroubles.event.SpawnProjectilesEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.Qualifier
import ktx.log.logger

@AllOf([PlayerComponent::class])
class SpawnProjectilesSystem(
    @Qualifier("gameStage") private val gameStage: Stage,
) : EventListener, IteratingSystem() {

    private var spawnTime: Float = 7f
    private var numberOfProjectiles: Int = 0
    private var canSpawn: Boolean = false;

    override fun onTickEntity(entity: Entity) {
        if (!canSpawn) {
            return;
        }

        if (spawnTime > 0f) {
            spawnTime -= deltaTime
//            log.debug { "Time: $spawnTime" }
            return;
        }
        spawnTime = 7f
        numberOfProjectiles = MathUtils.random(1, 2)
        gameStage.fire(SpawnProjectilesEvent(numberOfProjectiles))
    }

    override fun handle(event: Event): Boolean {
        return when (event) {
            is MapChangeEvent -> {
                canSpawn = true//MathUtils.random(0, 1) == 1
                true
            }

            else -> false
        }
    }

    companion object {
        private val log = logger<SpawnProjectilesSystem>()
    }
}