package com.github.manu.dungeonintroubles.ai

import com.github.manu.dungeonintroubles.component.AnimationType

enum class NpcsState : EntityState {
    IDLE {
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.IDLE)
        }

        override fun update(entity: AiEntity) {
            when {
                entity.wantsToRun -> entity.state(RUN)
            }
        }
    },
    RUN {
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.RUN)
        }
    },
    DEAD
}