package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
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

/**
 * Sistema que se encarga del movimiento en salto de las entidades con JUmpComponent
 *
 * @property prefs Datos guardados del juego, se incializa de forma automatica
 * @property physicsCmps Conjunto de entidades con PhysicComponent, se inicializa de forma automatica
 * @property jumpCmps Conjunto de entidades con JumpComponent, se inicializa de forma automatica
 */
@AllOf([JumpComponent::class, PhysicComponent::class])
class JumpMoveSystem(
    private val prefs: Preferences,
    private val physicsCmps: ComponentMapper<PhysicComponent>,
    private val jumpCmps: ComponentMapper<JumpComponent>,
) : IteratingSystem() {

    /**
     * Por cada entidad se calcula el impulsa en el eje y que se va aplicar en el sistema de fisicas
     *
     * @param entity Entidad a ejecutar
     *
     */
    override fun onTickEntity(entity: Entity) {
        val physcmp = physicsCmps[entity]
        val jumpCmp = jumpCmps[entity]

        //Calcula el impulsa con el giroscopio o si se pulsa la pantalla
        if (prefs.getBoolean("accelerometer")) {
            log.debug { "Z: ${Gdx.input.accelerometerZ}" }
            jumpCmp.sin = if (Gdx.input.accelerometerZ >= 8f) 1f else 0f
            physcmp.impulse.y = if (Gdx.input.accelerometerZ >= 8f) jumpCmp.speed else 0f
        } else {
            physcmp.impulse.y = if (jumpCmp.sin != 0f) jumpCmp.speed else 0f
        }
    }

    companion object {
        private val log = logger<JumpMoveSystem>()
    }
}