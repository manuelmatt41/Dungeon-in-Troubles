package com.github.manu.dungeonintroubles.ui.view

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.I18NBundle
import com.github.manu.dungeonintroubles.event.HideCreditsEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.manu.dungeonintroubles.ui.*
import com.github.manu.dungeonintroubles.ui.widget.PausePopUpBundle
import ktx.actors.alpha
import ktx.actors.onClick
import ktx.scene2d.*

enum class CreditsViewBundle {
    LBCREDITS, LBASSETS, LBMUSIC, LBTHANKS, LBCOUPLE, LBBROTHER, LBFAMILY, LBFRIENDS;

    var bundle: String = "CreditsView.${this.toString().lowercase()}"
}

class CreditsView(
    skin: Skin, prefs: Preferences, bundle: I18NBundle
) : Table(), KTable {

    init {
        setFillParent(true)

        table {
            background = skin[Drawables.PAUSE_POPUP_BACKGROUND]

            scrollPane(style = ScrollPanes.DEFAULT.skinKey) {
                table {
                    label(text = bundle[CreditsViewBundle.LBCREDITS.bundle], style = Labels.TITLE.skinKey) {
                        setSize(100f, 100f)
                        it.padLeft(7f).padTop(7f).top().row()
                    }

                    label(text = bundle[CreditsViewBundle.LBASSETS.bundle], style = Labels.DEFAULT.skinKey) {
                        it.padLeft(7f).padTop(7f).row()
                    }

                    table {
                        label(text = "Dungeon Tileset II and extensions", style = Labels.DEFAULT.skinKey) {
                            color = Color.GOLD
                            it.padLeft(7f).padTop(7f)
                        }

                        label(text = "0x72 in itch.io", style = Labels.DEFAULT.skinKey) {
                            it.padLeft(7f).padTop(7f).row()
                        }

                        label(text = "Cryo's Mini GUI", style = Labels.DEFAULT.skinKey) {
                            color = Color.GOLD
                            it.padLeft(7f).padTop(7f)
                        }

                        label(text = "PaperHatLizar in itch.io", style = Labels.DEFAULT.skinKey) {
                            it.padLeft(7f).padTop(7f).row()
                        }

                        label(text = "Free Pixel Effects Pack #13 - Fireballs", style = Labels.DEFAULT.skinKey) {
                            color = Color.GOLD
                            it.padLeft(7f).padTop(7f)
                        }

                        label(text = "XYEzawr in itch.io", style = Labels.DEFAULT.skinKey) {
                            it.padLeft(7f).padTop(7f).row()
                        }

                        label(text = "FXPack_", style = Labels.DEFAULT.skinKey) {
                            color = Color.GOLD
                            it.padLeft(7f).padTop(7f)
                        }

                        label(text = "Nyknck in itch.io", style = Labels.DEFAULT.skinKey) {
                            it.padLeft(7f).padTop(7f).row()
                        }

                        label(text = "Sci-fi Gateway", style = Labels.DEFAULT.skinKey) {
                            color = Color.GOLD
                            it.padLeft(7f).padTop(7f)
                        }

                        label(text = "jcrown41 in OpenGameArt.com", style = Labels.DEFAULT.skinKey) {
                            it.padLeft(7f).padTop(7f).row()
                        }

                        label(text = "Super Pixel Effects Mini Packs 1", style = Labels.DEFAULT.skinKey) {
                            color = Color.GOLD
                            it.padLeft(7f).padTop(7f)
                        }

                        label(text = "untiedGames (Will Tice) in itch.io", style = Labels.DEFAULT.skinKey) {
                            it.padLeft(7f).padTop(7f).row()
                        }
                        it.row()
                    }

                    label(text = bundle[CreditsViewBundle.LBMUSIC.bundle], style = Labels.DEFAULT.skinKey) {
                        it.padLeft(7f).padTop(7f).row()
                    }

                    table {
                        label(text = "4 Chiptunes (Adventure)", style = Labels.DEFAULT.skinKey) {
                            color = Color.GOLD
                            it.padLeft(7f).padTop(7f)
                        }

                        label(text = "SubspaceAudio in OpenGameArt.com", style = Labels.DEFAULT.skinKey) {
                            it.padLeft(7f).padTop(7f).row()
                        }

                        label(text = "10 8bit coin sounds", style = Labels.DEFAULT.skinKey) {
                            color = Color.GOLD
                            it.padLeft(7f).padTop(7f)
                        }

                        label(text = "Luke.RUSTLTD in OpenGameArt.com", style = Labels.DEFAULT.skinKey) {
                            it.padLeft(7f).padTop(7f).row()
                        }

                        label(text = "Spell 4 (fire)", style = Labels.DEFAULT.skinKey) {
                            color = Color.GOLD
                            it.padLeft(7f).padTop(7f)
                        }

                        label(text = "Bart K. in OpenGameArt.com", style = Labels.DEFAULT.skinKey) {
                            it.padLeft(7f).padTop(7f).row()
                        }

                        label(text = "Portal sound", style = Labels.DEFAULT.skinKey) {
                            color = Color.GOLD
                            it.padLeft(7f).padTop(7f)
                        }

                        label(text = "IgnasD in OpenGameArt.com", style = Labels.DEFAULT.skinKey) {
                            it.padLeft(7f).padTop(7f).row()
                        }

                        label(text = "Retro dead/destroyed/damaged sound", style = Labels.DEFAULT.skinKey) {
                            color = Color.GOLD
                            it.padLeft(7f).padTop(7f)
                        }

                        label(text = "Prinsu-Kun in OpenGameArt.com", style = Labels.DEFAULT.skinKey) {
                            it.padLeft(7f).padTop(7f).row()
                        }
                        it.row()
                    }

                    label(text = bundle[CreditsViewBundle.LBTHANKS.bundle], style = Labels.DEFAULT.skinKey) {
                        it.row()
                    }

                    table {
                        label(text = bundle[CreditsViewBundle.LBCOUPLE.bundle], style = Labels.DEFAULT.skinKey) {
                            color = Color.GOLD
                            it.padLeft(7f).padTop(7f)
                        }

                        label(text = "Nerea Cacebelos", style = Labels.DEFAULT.skinKey) {
                            it.padLeft(7f).padTop(7f).row()
                        }

                        label(text = bundle[CreditsViewBundle.LBBROTHER.bundle], style = Labels.DEFAULT.skinKey) {
                            color = Color.GOLD
                            it.padLeft(7f).padTop(7f)
                        }

                        label(text = "David Marin", style = Labels.DEFAULT.skinKey) {
                            it.padLeft(7f).padTop(7f).row()
                        }

                        label(text = bundle[CreditsViewBundle.LBFAMILY.bundle], style = Labels.DEFAULT.skinKey) {
                            color = Color.GOLD
                            it.padLeft(7f).padTop(7f)
                        }

                        label(text = "Mis padres y abuelos", style = Labels.DEFAULT.skinKey) {
                            it.padLeft(7f).padTop(7f).row()
                        }

                        label(text = bundle[CreditsViewBundle.LBFRIENDS.bundle], style = Labels.DEFAULT.skinKey) {
                            color = Color.GOLD
                            it.padLeft(7f).padTop(7f)
                        }

                        it.row()
                    }

                    textButton(text = bundle[PausePopUpBundle.BTBACK.bundle], style = Buttons.DEFAULT.skinKey) {
                        MenuView.attachTextMovement(this)

                        onClick {
                            stage.fire(HideCreditsEvent())
                        }
                        it.row()
                    }
                }

                it.pad(10f)
            }
            it.expand().fill()
        }
        alpha = 0f
        touchable = Touchable.disabled
    }
}

@Scene2dDsl
fun <S> KWidget<S>.creditsView(
    bundle: I18NBundle, prefs: Preferences, skin: Skin = Scene2DSkin.defaultSkin, init: CreditsView.(S) -> Unit = {}
): CreditsView = actor(CreditsView(skin, prefs, bundle), init)