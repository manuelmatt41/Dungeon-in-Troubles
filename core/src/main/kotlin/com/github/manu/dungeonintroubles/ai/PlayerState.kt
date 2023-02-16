package com.github.manu.dungeonintroubles.ai

import com.github.manu.dungeonintroubles.component.AnimationType

enum class PlayerState : EntityState {
    RUN {
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.RUN)
        }

        override fun update(entity: AiEntity) {
            when {
                entity.wantsToFly -> entity.state(FLY)
            }
        }
    },
    FLY {
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.HIT)
        }

        override fun update(entity: AiEntity) {
            when {
                !entity.wantsToFly -> entity.state(RUN)
            }
        }
    }
}