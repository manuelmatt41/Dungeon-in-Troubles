package com.github.manu.dungeonintroubles.ui.widget

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Scaling
import com.github.manu.dungeonintroubles.ui.Drawables
import com.github.manu.dungeonintroubles.ui.Labels
import com.github.manu.dungeonintroubles.ui.get
import ktx.actors.plusAssign
import ktx.log.logger
import ktx.scene2d.*

/**
 * Clase que representa un componente de la UI, que es un menu al pausar el juego
 *
 * @param charDrawables Imagen que representa a skin que esta usando el jugador
 * @param skin Skin de los componentes
 */
@Scene2dDsl
class PlayerInfo(
    charDrawables: Drawables,
    private val skin: Skin,
) : WidgetGroup(), KGroup {
    /**
     * Imagen que es el fondo del componente
     */
    private val background: Image = Image(skin[Drawables.PLAYER_INFO])

    /**
     * Imagen que representa la skin del jugador
     */
    private val playerBgd: Image = Image(skin[charDrawables])

    /**
     * Etiqueta que muestra la distancia del jugador durante la partida
     */
    val labelDistance: Label

    /**
     * Etiqueta que muestra las monedas que tiene el jugador en la partida
     */
    val labelCoins: Label

    /**
     * Inicia la clase y sus componentes
     */
    init {
        this += background
        this += playerBgd.apply {
            setPosition(2f, 2f)
            setSize(22f, 20f)
            setScaling(Scaling.contain)
        }

        labelDistance = label("", Labels.DEFAULT.skinKey).apply {
            setPosition(28f, 13f)
        }

        labelCoins = label("", Labels.DEFAULT.skinKey).apply {
            setPosition(35f, 2f)
        }

        this += labelDistance
        this += labelCoins

        touchable = Touchable.disabled
    }
    /**
     * Devuelve el ancho minimo del componente
     */
    override fun getPrefWidth() = background.drawable.minWidth
    /**
     * Devuelve el altura minimo del componente
     */
    override fun getPrefHeight() = background.drawable.minHeight

    /**
     * Cambia el texto de la etiqueta de las monedas
     *
     * @param coins Cantidad de monedas que se van a mostrar
     */
    fun getCoin(coins: Int) = labelCoins.setText(coins.toString())

    /**
     * Cambia el texto de la etiqueta de la distancia
     *
     * @param distance Distancia recorrida que se va mostrar
     */
    fun move(distance: Float) = labelDistance.setText(String.format("%.2f m", distance))
    companion object {
        private val log = logger<PlayerInfo>()
    }
}

/**
 * Extension que hace de contructor de la vista al crearla directamente a un actor para los escenarios
 */
@Scene2dDsl
fun <S> KWidget<S>.playerInfo(
    charDrawable: Drawables,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: PlayerInfo.(S) -> Unit = {}
): PlayerInfo = actor(PlayerInfo(charDrawable, skin), init)