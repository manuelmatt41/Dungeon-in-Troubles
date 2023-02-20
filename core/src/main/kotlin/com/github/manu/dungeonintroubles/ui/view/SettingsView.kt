package com.github.manu.dungeonintroubles.ui.view

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.I18NBundle
import com.github.manu.dungeonintroubles.event.ChangeSettingsEvent
import com.github.manu.dungeonintroubles.event.HideSettingsEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.manu.dungeonintroubles.ui.*
import com.github.manu.dungeonintroubles.ui.widget.PausePopUpBundle
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.actors.onTouchDown
import ktx.scene2d.*

/**
 * Enumerado para llamar al bundle de idiomas de forma mas legible
 */
enum class SettingsViewBundle {
    LBMUSIC, LBSOUND, CHVIBRATE, CHACCELEROMTER, BTAPPLY, BTCANCEL, LBSONG;
    /**
     * Convierte el enumerado en la Key del bundle
     */
    var bundle: String = "SettingsView.${this.toString().lowercase()}"
}

/**
 * Vista que representa los ajustes del juego, donde se puede modificar parametros generales como el volumen de la musica o sonidos, activar la vibracion o acelerometro
 *
 * @param skin Skin de los componentes
 * @param bundle Conjunto de cadenas que pueden ser traducidas
 * @param prefs Datos guardados del juego
 */
class SettingsView(
    skin: Skin,
    prefs: Preferences,
    bundle: I18NBundle
) : Table(skin), KTable {

    /**
     * Tabla que contiene los componente
     */
    val table: Table

    /**
     * Etiqueta que muesta el nivel de volumen de la musica
     */
    lateinit var labelMusic: Label

    /**
     * Deslizador para cambiar el valor del volumen de la musica
     */
    var sliderMusic: Slider

    /**
     *Etiqueta que muesta el nivel de volumen de los efectos de sonido
     */
    lateinit var labelEffects: Label

    /**
     * Deslizador para cambiar el valor del volumen de los efectos de sonido
     */
    var sliderEffects: Slider

    /**
     * Casilla para activar la vibracion
     */
    var cbVibrate: CheckBox

    /**
     * Casilla para activar el acelerometro
     */
    var cbAccelerometer: CheckBox

    /**
     * Casilla para activar la primera cancion
     */
    var cbSong1: CheckBox

    /**
     * Casila para activar la segunda cancion
     */
    lateinit var cbSong2: CheckBox // TODO Do a select option with all songs

    /**
     * Casilla para activar la tercera cancion
     */
    lateinit var cbSong3: CheckBox

    /**
     * Inicia la vista y sus componentes
     */
    init {
        setFillParent(true)

        table = table {
            background = skin[Drawables.PAUSE_POPUP_BACKGROUND]

            table {
                label(text = bundle[MenuViewBundle.BTSETTING.bundle], style = Labels.TITLE.skinKey) {
                    it.padTop(7f).padLeft(7f).row()
                }
                //Music volume
                table {
                    label(text = bundle[SettingsViewBundle.LBMUSIC.bundle], style = Labels.DEFAULT.skinKey) {
                        it.padTop(2f).padRight(2f)
                    }

                    this@SettingsView.sliderMusic = slider(0f, 100f, 1f, false, Sliders.DEFAULT.skinKey) {
                        value = if (prefs.contains("music")) prefs.getInteger("music").toFloat() else 100f
                        onChange {
                            this@SettingsView.labelMusic.setText(value.toInt().toString())
                        }

                    }

                    this@SettingsView.labelMusic =
                        label(text = prefs.getInteger("music").toString(), style = Labels.DEFAULT.skinKey) {
                           setText(if (prefs.contains("music")) prefs.getInteger("music").toString() else 100.toString())
                            it.width(10f).padTop(2f).padLeft(2f)
                        }

                    it.padBottom(10f).row()
                }
                //Sound volume
                table {
                    label(text = bundle[SettingsViewBundle.LBSOUND.bundle], style = Labels.DEFAULT.skinKey) {
                        it.padTop(2f).padRight(2f)
                    }

                    this@SettingsView.sliderEffects = slider(0f, 100f, 1f, false, Sliders.DEFAULT.skinKey) {
                        value = if (prefs.contains("sound")) prefs.getInteger("sound").toFloat() else 100f

                        onChange {
                            this@SettingsView.labelEffects.setText(value.toInt().toString())
                        }

                    }

                    this@SettingsView.labelEffects =
                        label(text = prefs.getInteger("sound").toString(), style = Labels.DEFAULT.skinKey) {
                           setText(if (prefs.contains("music")) prefs.getInteger("music").toString() else 100.toString())
                            it.width(10f).padTop(2f).padLeft(2f)
                        }

                    it.padBottom(10f).row()
                }

                table {
                    this@SettingsView.cbVibrate = checkBox(text = "", style = CheckBoxes.DEFAULT.skinKey) {
                        isChecked = prefs.getBoolean("vibrate")
                        it.padRight(2f)
                    }

                    label(text = bundle[SettingsViewBundle.CHVIBRATE.bundle])
                    it.padBottom(10f).left().row()
                }

                table {
                    this@SettingsView.cbAccelerometer = checkBox(text = "", style = CheckBoxes.DEFAULT.skinKey) {
                        isChecked = prefs.getBoolean("accelerometer")

                        it.padRight(2f)
                    }

                    label(text = bundle[SettingsViewBundle.CHACCELEROMTER.bundle])
                    it.padBottom(10f).left().row()
                }

                table {
                    label(text = bundle[SettingsViewBundle.LBSONG.bundle], style = Labels.DEFAULT.skinKey) {
                        it.padRight(5f)
                    }

                    this@SettingsView.cbSong1 = checkBox(text = "  1", style = CheckBoxes.DEFAULT.skinKey) {
                        isChecked =
                            prefs.getString("song") == "1.ogg" //TODO Make this better
                        onChange {
                            if (isChecked) {
                                this@SettingsView.cbSong2.isChecked = false
                                this@SettingsView.cbSong3.isChecked = false

                            }
                        }
                        it.padRight(5f)
                    }

                    this@SettingsView.cbSong2 = checkBox(text = "  2", style = CheckBoxes.DEFAULT.skinKey) {
                        isChecked = prefs.getString("song") == "2.ogg"

                        onChange {
                            if (isChecked) {
                                this@SettingsView.cbSong1.isChecked = false
                                this@SettingsView.cbSong3.isChecked = false

                            }
                        }
                        it.padRight(5f)
                    }

                    this@SettingsView.cbSong3 = checkBox(text = "  3", style = CheckBoxes.DEFAULT.skinKey) {
                        isChecked = prefs.getString("song") == "3.ogg"

                        onChange {
                            if (isChecked) {
                                this@SettingsView.cbSong2.isChecked = false
                                this@SettingsView.cbSong1.isChecked = false

                            }
                        }
                        it.padRight(5f)
                    }

                    it.padBottom(10f).left().row()
                }

                table {
                    textButton(text = bundle[SettingsViewBundle.BTAPPLY.bundle], style = Buttons.DEFAULT.skinKey) {
                        MenuView.attachTextMovement(this)

                        onTouchDown {
                            prefs.putInteger("music", this@SettingsView.labelMusic.text.toString().toInt())
                            prefs.putInteger("sound", this@SettingsView.labelEffects.text.toString().toInt())
                            prefs.putBoolean("vibrate", this@SettingsView.cbVibrate.isChecked)
                            prefs.putBoolean("accelerometer", this@SettingsView.cbAccelerometer.isChecked)

                            if (this@SettingsView.cbSong1.isChecked) {
                                prefs.putString("song", "1.ogg")
                            } else if (this@SettingsView.cbSong2.isChecked) {
                                prefs.putString("song", "2.ogg") //TODO This need a refactor
                            } else if (this@SettingsView.cbSong3.isChecked) {
                                prefs.putString("song", "3.ogg")
                            } else {
                                prefs.putString("song", "1.ogg")
                            }

                            prefs.flush()
                            stage.fire(ChangeSettingsEvent())
                        }
                        it.padRight(3f)
                    }

                    textButton(text = bundle[SettingsViewBundle.BTCANCEL.bundle], style = Buttons.DEFAULT.skinKey) {
                        MenuView.attachTextMovement(this)

                        onClick {
                            this@SettingsView.labelMusic.setText(prefs.getInteger("music"))
                            this@SettingsView.sliderMusic.value = prefs.getInteger("music").toFloat()
                            this@SettingsView.labelMusic.setText(prefs.getInteger("music"))
                            this@SettingsView.sliderEffects.value = prefs.getInteger("sound").toFloat()
                            this@SettingsView.labelEffects.setText(prefs.getInteger("sound"))
                            this@SettingsView.cbVibrate.isChecked = prefs.getBoolean("vibrate")
                            this@SettingsView.cbAccelerometer.isChecked = prefs.getBoolean("accelerometer")
                        }

                        it.padRight(3f)
                    }

                    textButton(text = bundle[PausePopUpBundle.BTBACK.bundle], style = Buttons.DEFAULT.skinKey) {
                        MenuView.attachTextMovement(this)

                        onClick {
                            stage.fire(HideSettingsEvent())
                        }

                        it.padRight(3f)
                    }
                }

                it.expand().top().padLeft(10f).padTop(10f)
            }

            it.expand().fill()
        }
        pad(5f)
    }
}

/**
 * Extension que hace de contructor de la vista al crearla directamente a un actor para los escenarios
 */
@Scene2dDsl
fun <S> KWidget<S>.settingsView(
    bundle: I18NBundle,
    prefs: Preferences,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: SettingsView.(S) -> Unit = {}
): SettingsView = actor(SettingsView(skin, prefs, bundle), init)