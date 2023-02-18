package com.github.manu.dungeonintroubles.ai

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram

/**
 * Interfaz para el sistema de estados para que funcione con la AiEntity
 */
interface EntityState : State<AiEntity> {
    /**
     * Se ejecuta cuando entra en el estado
     */
    override fun enter(entity: AiEntity) = Unit

    /**
     * Se ejecuta todo el tiempo que el estado este establecido
     */
    override fun update(entity: AiEntity) = Unit

    /**
     * Se ejecuta cuando sale del estado
     */
    override fun exit(entity: AiEntity) = Unit

    /**
     * Para mandar mensajes a otras entidad en un sistema de mensajes de la maquina de estados
     */
    override fun onMessage(entity: AiEntity, telegram: Telegram) = false
}