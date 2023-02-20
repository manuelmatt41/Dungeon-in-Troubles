package com.github.manu.dungeonintroubles.ui.widget


import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.I18NBundle
import com.github.manu.dungeonintroubles.event.SetGameEvent
import com.github.manu.dungeonintroubles.event.SetMenuScreenEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.manu.dungeonintroubles.ui.Buttons
import com.github.manu.dungeonintroubles.ui.Drawables
import com.github.manu.dungeonintroubles.ui.Labels
import com.github.manu.dungeonintroubles.ui.get
import com.github.manu.dungeonintroubles.ui.view.MenuView
import com.github.manu.dungeonintroubles.ui.view.MenuViewBundle
import ktx.actors.onClick
import ktx.actors.plusAssign
import ktx.scene2d.*

/**
 * Clase que representa un componente de la UI, que es un menu al perder la partida
 *
 * @param skin Skin de los componentes
 * @param bundle Conjunto de cadenas que pueden ser traducidas
 * @param prefs Datos guardados del juego
 * @param distance Distancia recorrida por el jugador para establecer si es nuevo record
 */
class DeathPopUp(
    skin: Skin, bundle: I18NBundle, prefs: Preferences, distance: Float
) : WidgetGroup(), KGroup {

    /**
     * Imagen que es el fondo del componente
     */
    private val background: Image = Image(skin[Drawables.PAUSE_POPUP_BACKGROUND])

    /**
     * Tabla que contiene los componentes que componen la clase
     */
    private val table: Table

    /**
     * Inicia la clase y sus componentes
     */
    init {
        this += background

        table = table {

            if (distance > prefs.getFloat("distance")) {
                label(text = "New record!", style = Labels.TITLE.skinKey) {
                    color = Color.GREEN

                    it.padTop(15f).row()
                }
            }

            label(
                text = "Your distance: ${String.format("%.2f m", distance)}"
            ) {
                it.row()
            }

            label(
                text = "Your BEST distance: ${String.format("%.2f m", prefs.getFloat("distance"))}",
                style = Labels.DEFAULT.skinKey
            ) {
                color = Color.GOLD
                it.row()
            }

            textButton(text = bundle[MenuViewBundle.BTNEWGAME.bundle], style = Buttons.DEFAULT.skinKey) {
                MenuView.attachTextMovement(this)
                onClick { stage.fire(SetGameEvent()) }
                label.y -= 2

                it.padBottom(10f)
                it.row()
            }

            textButton(text = bundle[PausePopUpBundle.BTBACK.bundle], style = Buttons.DEFAULT.skinKey) {
                MenuView.attachTextMovement(this)
                onClick { stage.fire(SetMenuScreenEvent()) }
                label.y -= 2

                it.padBottom(10f)
                it.row()
            }

            setPosition(this@DeathPopUp.background.width * 0.5f, this@DeathPopUp.background.height * 0.5f)
        }

        this += table
    }

    /**
     * Devuelve el ancho minimo del componente
     */
    override fun getPrefWidth() = background.drawable.minWidth

    /**
     * Devuelve el altura minimo del componente
     */
    override fun getPrefHeight() = background.drawable.minHeight
}

/**
 * Extension que hace de contructor de la vista al crearla directamente a un actor para los escenarios
 */
@Scene2dDsl
fun <S> KWidget<S>.deathPopUp(
    skin: Skin = Scene2DSkin.defaultSkin,
    bundle: I18NBundle,
    prefs: Preferences,
    distance: Float,
    coins: Int,
    init: DeathPopUp.(S) -> Unit = {}
): DeathPopUp = actor(DeathPopUp(skin, bundle, prefs, distance), init)