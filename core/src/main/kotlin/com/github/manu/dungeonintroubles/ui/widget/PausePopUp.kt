package com.github.manu.dungeonintroubles.ui.widget

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.I18NBundle
import com.github.manu.dungeonintroubles.event.GameResumeEvent
import com.github.manu.dungeonintroubles.event.SetMenuScreenEvent
import com.github.manu.dungeonintroubles.event.ShowSettingsEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.manu.dungeonintroubles.ui.Buttons
import com.github.manu.dungeonintroubles.ui.Drawables
import com.github.manu.dungeonintroubles.ui.get
import com.github.manu.dungeonintroubles.ui.view.MenuView
import com.github.manu.dungeonintroubles.ui.view.MenuViewBundle
import ktx.actors.onClick
import ktx.actors.plusAssign
import ktx.scene2d.*

/**
 * Enumerado para llamar al bundle de idiomas de forma mas legible
 */
enum class PausePopUpBundle {
    BTRESUME, BTBACK;

    /**
     * Convierte el enumerado en la Key del bundle
     */
    var bundle: String = "PausePopUp.${this.toString().lowercase()}"
}

/**
 * Clase que representa un componente de la UI, que es un menu al pausar el juego
 *
 * @param skin Skin de los componentes
 * @param bundle Conjunto de cadenas que pueden ser traducidas
 */
class PausePopUp(
    private val skin: Skin,
    bundle: I18NBundle
) : WidgetGroup(), KGroup {

    /**
     * Imagen que es el fondo del componente
     */
    private val background: Image = Image(skin[Drawables.PAUSE_POPUP_BACKGROUND])

    /**
     * Tabla que contiene lo componentes que componen la clase
     */
    private val table: Table

    /**
     * Inicia la clase y sus componentes
     */
    init {
        this += background

        table = table {
            textButton(text = bundle[PausePopUpBundle.BTRESUME.bundle], style = Buttons.DEFAULT.skinKey) {
                MenuView.attachTextMovement(this)
                onClick { stage.fire(GameResumeEvent()) }

                it.padBottom(10f)
                it.row()
            }

            textButton(text = bundle[MenuViewBundle.BTSETTING.bundle], style = Buttons.DEFAULT.skinKey) {
                MenuView.attachTextMovement(this)
                onClick { stage.fire(ShowSettingsEvent()) }

                it.padBottom(10f)
                it.row()
            }

            textButton(text = bundle[PausePopUpBundle.BTBACK.bundle], style = Buttons.DEFAULT.skinKey) {
                MenuView.attachTextMovement(this)
                onClick { stage.fire(SetMenuScreenEvent()) }

                it.padBottom(10f)
                it.row()
            }

            setPosition(this@PausePopUp.background.width * 0.5f, this@PausePopUp.background.height * 0.5f)
        }

        this += table
    }
    /**
     * Devuelve el ancho minimo del componente
     */
    override fun getPrefWidth() = background.drawable.minWidth
    /**
     * Devuelve la altura minimo del componente
     */
    override fun getPrefHeight() = background.drawable.minHeight
}

/**
 * Extension que hace de contructor de la vista al crearla directamente a un actor para los escenarios
 */
@Scene2dDsl
fun <S> KWidget<S>.pausePopUp(
    skin: Skin = Scene2DSkin.defaultSkin,
    bundle: I18NBundle,
    init: PausePopUp.(S) -> Unit = {}
): PausePopUp = actor(PausePopUp(skin, bundle), init)