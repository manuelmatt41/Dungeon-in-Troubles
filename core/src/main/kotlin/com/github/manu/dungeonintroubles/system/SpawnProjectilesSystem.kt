package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.github.manu.dungeonintroubles.DungeonInTroubles.Companion.UNIT_SCALE
import com.github.manu.dungeonintroubles.component.AlertProjectileComponent
import com.github.manu.dungeonintroubles.component.EntityType
import com.github.manu.dungeonintroubles.component.PlayerComponent
import com.github.manu.dungeonintroubles.component.SpawnComponent
import com.github.manu.dungeonintroubles.event.MapChangeEvent
import com.github.manu.dungeonintroubles.event.SpawnProjectilesSoundEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.Qualifier
import ktx.log.logger

/**
 * Sistema que se encarga de hacer aparecer los proyectiles en el juego
 *
 * @property gameStage Escenario que representa el juego, se incicializa de forma automatica
 * @property uiStage EScenario que representa la UI del juego, se inicializa de forma automatica
 * @property textureAtlas Atlas de texturas, se inicializa de forma automatica
 */
@AllOf([PlayerComponent::class])
class SpawnProjectilesSystem(
    @Qualifier("gameStage") private val gameStage: Stage,
    @Qualifier("uiStage") private val uiStage: Stage,
    private val textureAtlas: TextureAtlas,
) : EventListener, IteratingSystem() {
    /**
     * Tiempo que tarda en hacer aparecer mas proyectiles
     */
    private var spawnTime: Float = 9f

    /**
     * Numero de proyectiles que va a hacer aparecer
     */
    private var numberOfProjectiles: Int = 0

    /**
     * Valor que comprueba que pueda hacer aparecer proyectiles
     */
    private var canSpawn: Boolean = false

    /**
     * Fuente de como de la etiqueta que avisa del proyectil
     */
    private val minimalFont = BitmapFont(Gdx.files.internal("ui/minimalpixel.fnt")).apply { data.setScale(1.5f) }

    /**
     * Estilo de la etiqueta que avisa del proyectil
     */
    private val floatingTextStyle = LabelStyle(minimalFont, Color.RED).apply {
        background = TextureRegionDrawable(textureAtlas.findRegion("alert")).apply {// TODO Refactor to the ui view/model
            leftWidth = 6f
            topHeight = 8f
        }
    }

    /**
     * Por cada entidad comprueba que pueda hacer aparecer proyectiles, en caso que pueda los a??ade al mundo junto su aviso
     *
     * @param entity Entidad a ejecutar
     */
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

        var x = gameStage.camera.position.x + 60f;

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
            gameStage.fire(SpawnProjectilesSoundEvent())
            x += 5f
        }
    }

    /**
     * Crea y a??ade al mundo las alertas de los proyectiles
     *
     * @param y Posicion en el eje Y del poryectil para mostar la etiqueta
     */
    private fun alertProjectilesLabel(y: Float) {
        world.entity {
            add<AlertProjectileComponent> {
                label = Label("!", floatingTextStyle)
                position.set(uiStage.width - label.width * 2f, y)
            }
        }
    }

    /**
     * Se ejecuta cuando se lanza un evento y se comrpueba si la coge para ejecutar codigo
     */
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