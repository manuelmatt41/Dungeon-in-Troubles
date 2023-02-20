package com.github.manu.dungeonintroubles.ai

import com.github.manu.dungeonintroubles.component.AnimationType

/**
 * Estados que puede entrar el jugador
 */
enum class PlayerState : EntityState {
    /**
     * Estado que representa la accion de correr
     */
    RUN {
        /**
         * Comprueba que entra al estado de correr cambia la animación a correr
         * @param entity Entidad que entra en el estado
         */
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.RUN)
        }

        /**
         * En cada actualizacion en este estado omprueba si el jugador quier volar y cambia a ese estado en consecuencia
         * @param entity Entidad que actualiza el estado
         */
        override fun update(entity: AiEntity) {
            when {
                entity.wantsToFly -> entity.state(FLY)
            }
        }
    },

    /**
     * Estado que representa la acción volar
     */
    FLY {
        /**
         * Comprueba que entra al estado volar
         *
         * @param entity Entidad que entra en el estado
         */
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.FLY)
        }

        /**
         * Comprueba si la entidad quiere dejar de volar y volver a correr
         *
         * @param entity Entidad que actualiza el estado
         */
        override fun update(entity: AiEntity) {
            when {
                !entity.wantsToFly -> entity.state(RUN)
            }
        }
    }
}