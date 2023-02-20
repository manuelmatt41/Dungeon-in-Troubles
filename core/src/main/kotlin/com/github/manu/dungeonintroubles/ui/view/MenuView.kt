package com.github.manu.dungeonintroubles.ui.view

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.I18NBundle
import com.github.manu.dungeonintroubles.event.*
import com.github.manu.dungeonintroubles.extension.fire
import com.github.manu.dungeonintroubles.ui.Buttons
import com.github.manu.dungeonintroubles.ui.Labels
import ktx.actors.*
import ktx.log.logger
import ktx.scene2d.*

/**
 * Enumerado para llamar al bundle de idiomas de forma mas legible
 */
enum class MenuViewBundle {
    BTNEWGAME, BTSTORE, BTSETTING, BTEXIT;
    /**
     * Convierte el enumerado en la Key del bundle
     */
    var bundle: String = "MenuView.${this.toString().lowercase()}"
}

/**
 * Vista que representa el menu principal del juego, donde se puede elegir varias opciones como empezar el juego, abrir la tienda, los ajustes, ver lo creditos o cerrar el juego
 *
 * @param skin Skin de los componentes
 * @param bundle Conjunto de cadenas que pueden ser traducidas
 * @param prefs Datos guardados del juego
 */
class MenuView(
    skin: Skin,
    bundle: I18NBundle,
    prefs: Preferences
) : Table(skin), KTable {

    /**
     * Componente boton para empezar el juego
     */
    private val btNewGame: TextButton

    /**
     * Componente boton para abrir la tienda del juego
     */
    private val btStoreMenu: TextButton

    /**
     * Componente boton para abrir los ajustes del juego
     */
    private val btSetting: TextButton

    /**
     * Componente boton para mostrar los creditos del juego
     */
    private val btCredits: TextButton

    /**
     * Componente boton para salir del juego
     */
    private val btExit: TextButton


    /**
     * Inicia la vista y sus componentes
     */
    init {
        log.debug { "${prefs.getInteger("coins")}" }
        setFillParent(true)

        table {

            table {

                label(text = "Dungeon\n  in\nTroubles", style = Labels.TITLE.skinKey) {
                    setSize(100f, 100f)
                    it.center().row()
                }

                this@MenuView.btNewGame =
                    textButton(text = bundle[MenuViewBundle.BTNEWGAME.bundle], style = Buttons.DEFAULT.skinKey) {
                        attachTextMovement(this)

                        it.padBottom(9f)
                        it.center().row()
                    }

                this@MenuView.btStoreMenu =
                    textButton(text = bundle[MenuViewBundle.BTSTORE.bundle], style = Buttons.DEFAULT.skinKey) {
                        attachTextMovement(this)

                        it.padBottom(9f)
                        it.center().row()
                    }

                this@MenuView.btSetting =
                    textButton(text = bundle[MenuViewBundle.BTSETTING.bundle], style = Buttons.DEFAULT.skinKey) {
                        attachTextMovement(this)
                        label.y -= 8

                        onClick {
                            stage.fire(ShowSettingsEvent())
                        }

                        it.padBottom(10f)
                        it.center().row()
                    }

                this@MenuView.btCredits =
                    textButton(text = bundle[CreditsViewBundle.LBCREDITS.bundle], style = Buttons.DEFAULT.skinKey) {
                        attachTextMovement(this)
                        label.y -= 2

                        onClick {
                            stage.fire(ShowCreditsEvent())
                        }

                        it.padBottom(10f)
                        it.row()
                    }

                this@MenuView.btExit =
                    textButton(text = bundle[MenuViewBundle.BTEXIT.bundle], style = Buttons.DEFAULT.skinKey) {
                        attachTextMovement(this)
                        label.y -= 2

                        it.padBottom(10f)
                        it.center()
                    }

                it.bottom().padLeft(5f)
            }

            table {
                label(text = "Version  1.0", style = Labels.DEFAULT.skinKey) {
                    it.padBottom(20f).padRight(12f).bottom().right().row()
                }

                label(
                    text = "Coins: ${prefs.getInteger("coins")}",
                    style = Labels.DEFAULT.skinKey,
                ) {
                    it.left().row()
                }

                label(
                    text = String.format("Distance: %.2f m", prefs.getFloat("distance")),
                    style = Labels.DEFAULT.skinKey
                ) {
                    it.left()
                }

                it.expand().top().right().padTop(5f).padRight(2f)
            }
            it.expand().fill()
        }

        btNewGame.onClick {
            if (prefs.getFloat("distance") == 0f) {
                stage.fire(ShowTutorialEvent())
            } else {
                stage.fire(SetGameEvent())
            }
        }

        btStoreMenu.onClick {
            stage.fire(ShowStoreEvent())
        }

        btSetting.onClick {
            stage.fire(ShowSettingsEvent())
        }

        btExit.onClick {
            stage.fire(ExitGameEvent())
        }
    }


    companion object {
        private val log = logger<MenuView>()

        /**
         * Vinvula el boton a varios eventos para hacer que la label que contiene los TextButton se muevan con la imagen
         *
         * @param button Boton que va a ser vinculado
         */
        fun attachTextMovement(button: KTextButton) {
            with(button) {
                onTouchDown {
                    button.label.y -= 2f //Move the label 2 pixel down with the buttton movement
                }

                onClick {
                    this.label.y += 2f //Move the label 2 pixel up with the buttton movement
                }

                onExit {
                    this.label.y = 0f
                }

                onEnter {
                    if (this.label.y == 0f && this.isPressed) {
                        this.label.y -= 2f
                    }
                }
            }
        }
    }
}

/**
 * Extension que hace de contructor de la vista al crearla directamente a un actor para los escenarios
 */
@Scene2dDsl
fun <S> KWidget<S>.menuView(
    skin: Skin = Scene2DSkin.defaultSkin,
    bundle: I18NBundle,
    prefs: Preferences,
    init: MenuView.(S) -> Unit = {}
): MenuView = actor(MenuView(skin, bundle, prefs), init)