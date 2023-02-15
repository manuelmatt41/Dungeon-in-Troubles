package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.github.manu.dungeonintroubles.DungeonInTroubles
import com.github.manu.dungeonintroubles.DungeonInTroubles.Companion.UNIT_SCALE
import com.github.manu.dungeonintroubles.component.AlertProjectileComponent
import com.github.manu.dungeonintroubles.component.EntityType
import com.github.manu.dungeonintroubles.component.PlayerComponent
import com.github.manu.dungeonintroubles.component.SpawnComponent
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
    @Qualifier("uiStage") private val uiStage: Stage,
) : EventListener, IteratingSystem() {

    private var spawnTime: Float = 7f
    private var numberOfProjectiles: Int = 0
    private var canSpawn: Boolean = false;
    private val damageFont = BitmapFont(Gdx.files.internal("ui/minimalpixel.fnt")).apply { data.setScale(2f) }
    private val floatingTextStyle = LabelStyle(damageFont, Color.RED)

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
        numberOfProjectiles = MathUtils.random(1, 3)

        var x = gameStage.camera.position.x + 20f;

        for (i in 1..numberOfProjectiles) {
            world.entity {
                add<SpawnComponent> {
                    this.name = EntityType.FIREBALL
                    this.location.set(
                        x,
                        MathUtils.random(2, 7).toFloat()
                    )
                    alertProjectilesLabel((this.location.y / UNIT_SCALE) + 40f)
                }
            }

            x += 5f
        }
    }

    private fun alertProjectilesLabel(y: Float) {
        log.debug { "Alert put" }
        world.entity {
            add<AlertProjectileComponent> {
                label = Label("!", floatingTextStyle)
                position.set(uiStage.width - label.width * 2f, y)
            }
        }
    }
    override fun handle(event: Event): Boolean {
        return when (event) {
            is MapChangeEvent -> {
                canSpawn = MathUtils.random(0, 1) == 1
                true
            }

            else -> false
        }
    }

    companion object {
        private val log = logger<SpawnProjectilesSystem>()
    }
}