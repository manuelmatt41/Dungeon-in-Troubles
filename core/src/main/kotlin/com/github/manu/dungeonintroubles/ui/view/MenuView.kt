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

enum class MenuViewBundle {
    BTNEWGAME, BTSTORE, BTSETTING, BTEXIT;

    var bundle: String = "MenuView.${this.toString().lowercase()}"
}

class MenuView(
    skin: Skin,
    bundle: I18NBundle,
    prefs: Preferences
) : Table(), KTable {

    private val btNewGame: TextButton
    private val btStoreMenu: TextButton
    private val btSetting: TextButton
    private val btCredits: TextButton
    private val btExit: TextButton

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

@Scene2dDsl
fun <S> KWidget<S>.menuView(
    skin: Skin = Scene2DSkin.defaultSkin,
    bundle: I18NBundle,
    prefs: Preferences,
    init: MenuView.(S) -> Unit = {}
): MenuView = actor(MenuView(skin, bundle, prefs), init)