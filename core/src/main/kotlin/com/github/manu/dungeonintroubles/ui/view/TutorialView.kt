package com.github.manu.dungeonintroubles.ui.view

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.Scaling
import com.github.manu.dungeonintroubles.event.HideTutorialEvent
import com.github.manu.dungeonintroubles.event.SetGameEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.manu.dungeonintroubles.ui.Drawables
import com.github.manu.dungeonintroubles.ui.Labels
import com.github.manu.dungeonintroubles.ui.get
import ktx.actors.alpha
import ktx.actors.onClick
import ktx.scene2d.*

/**
 * Enumerado para llamar al bundle de idiomas de forma mas legible
 */
enum class TutorialViewBundle {
    LBTAP, LBTRAP, LBFIREBALL, LBCOIN, LBPLAYER;
    /**
     * Convierte el enumerado en la Key del bundle
     */
    var bundle: String = "TutorialView.${this.toString().lowercase()}"

}

/**
 * Vista que representa el tutorial qu explica brevemente como funciona el juego
 *
 * @param bundle Conjunto de cadenas que pueden ser traducidas
 * @param prefs Datos guardados del juego
 * @param skin Skin de los componentes
 */
class TutorialView(
    bundle: I18NBundle,
    val prefs: Preferences,
    skin: Skin
) : Table(skin), KTable {

    /**
     * Inicia la vista y sus componentes
     */
    init {
        setFillParent(true)

        table {
            color = Color.BLACK

            table {
                image(skin[Drawables.TRAP]) {
                    setScaling(Scaling.fill)
                    it.width(25f).height(25f)
                }
                label(text = bundle[TutorialViewBundle.LBTRAP.bundle], style = Labels.DEFAULT.skinKey) {
                    it.padTop(10f).padLeft(10f).minWidth(200f).minHeight(25f).row()
                }

                image(skin[Drawables.FIREBALL]) {
                    setScaling(Scaling.contain)
                    it.width(25f).height(25f)
                }
                label(text = bundle[TutorialViewBundle.LBFIREBALL.bundle], style = Labels.DEFAULT.skinKey) {
                    wrap = true
                    it.minWidth(200f).minHeight(25f).row()
                }

                image(skin[Drawables.COIN]) {
                    setScaling(Scaling.fill)
                    it.width(25f).height(25f)
                }
                label(text = bundle[TutorialViewBundle.LBCOIN.bundle], style = Labels.DEFAULT.skinKey) {
                    wrap = true
                    it.padTop(10f).minWidth(200f).minHeight(25f).row()
                }

                image(skin[Drawables.DEFAULT]) {
                    setScaling(Scaling.fill)
                    it.width(25f).height(25f)
                }
                label(text = bundle[TutorialViewBundle.LBPLAYER.bundle], style = Labels.DEFAULT.skinKey) {
                    wrap = true
                    it.padTop(15f).minWidth(200f).minHeight(25f).row()
                }
                it.row()
            }
                label(text = bundle[TutorialViewBundle.LBTAP.bundle], style = Labels.DEFAULT.skinKey) {
                    wrap = true
                    it.minWidth(300f).minHeight(40f)
                }
            it.expand().fill().top()
        }

        onClick {
            stage.fire(HideTutorialEvent())
            stage.fire(SetGameEvent())
        }
    }
}

/**
 * Extension que hace de contructor de la vista al crearla directamente a un actor para los escenarios
 */
@Scene2dDsl
fun <S> KWidget<S>.tutorialView(
    bundle: I18NBundle,
    prefs: Preferences,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: TutorialView.(S) -> Unit = {}
): TutorialView = actor(TutorialView(bundle, prefs, skin), init)