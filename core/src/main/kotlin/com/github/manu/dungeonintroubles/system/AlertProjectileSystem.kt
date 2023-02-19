package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.manu.dungeonintroubles.DungeonInTroubles.Companion.UNIT_SCALE
import com.github.manu.dungeonintroubles.component.AlertProjectileComponent
import com.github.quillraven.fleks.*
import ktx.actors.alpha
import ktx.log.logger
import kotlin.math.log

/**
 * Sistema que recorre las entidades con AlertProjectileComponent y se encarga de enseñar las alertas de los proyectiles
 *
 * @property alertsCmps Entidades con AlertProjectileComponent que se inicia de forma automatica
 *
 * @constructor Inicia un AlertProjectileSystem con los componentes que vayan entrando
 */
@AllOf([AlertProjectileComponent::class])
class AlertProjectileSystem(
    private val alertsCmps: ComponentMapper<AlertProjectileComponent>
) : IteratingSystem() {
    /**
     * Por cada entidades que este en el sistema coloca la alerta en la posicion hasta un tiempo determinado y lo elimina
     * @param entity Entidad a ejecutar
     */
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