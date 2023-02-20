package com.github.manu.dungeonintroubles.ui.view

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.I18NBundle
import com.github.manu.dungeonintroubles.event.HideStoreEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.manu.dungeonintroubles.ui.*
import com.github.manu.dungeonintroubles.ui.widget.skinStoreRow
import com.github.manu.dungeonintroubles.ui.widget.PausePopUpBundle
import com.github.manu.dungeonintroubles.ui.widget.SkinStoreRow
import ktx.actors.onClick
import ktx.scene2d.*

/**
 * Enumerado que representa la cantidad de skins que tiene el jugador para el juego
 *
 * @param coin Monedas que vale la skin para poder comprar
 */
enum class PlayerSkins(var coin: Int) {
    DEFAULT(0),
    ELF_F(100),
    ELF_M(100),
    KNIGHT_F(200),
    KNIGHT_M(200),
    WITCH(500),
    WITCHER(500),
    LIZARD(1000),
    PUMPKIN_DUDE(1500),
    DOCTOR(2000);
    /**
     * Convierte el enumerado en la key para el texture atlas
     */
    var atlasKey: String = this.toString().lowercase()
}

/**
 * Vista que representa la tienda del juego donde se puede gastar las monedas del juego comprando skins para el personaje y la partida
 *
 * @param skin Skin de los componentes
 * @param playerPrefs Datos guardados del jugador del juego
 * @param settingsPrefs Datos guardados de los ajustes del juego
 * @param bundle Conjunto de cadenas que pueden ser traducidas
 */
class StoreView(
    skin: Skin,
    playerPrefs: Preferences,
    settingsPrefs: Preferences,
    bundle: I18NBundle
) : Table(skin), KTable {

    /*+
    Tabal que contiene los componentes para comprar skins
     */
    var skinsTable: Table

    /**
     * Inicia la vista y sus componentes
     */
    init {
        setFillParent(true)

        table {
            background = skin[Drawables.PAUSE_POPUP_BACKGROUND]

            scrollPane(style = ScrollPanes.DEFAULT.skinKey) {
                this@StoreView.skinsTable = table {
                    label(text = bundle[MenuViewBundle.BTSTORE.bundle], style = Labels.TITLE.skinKey) {
                        setSize(100f, 100f)
                        it.padLeft(7f).padTop(7f).top().minWidth(200f).row()
                    }

                    PlayerSkins.values().forEach { playerSkin ->
                        skinStoreRow(
                            enumValueOf(playerSkin.name),
                            playerSkin,
                            settingsPrefs.getBoolean("skin/${playerSkin.name}"),
                            bundle,
                            playerPrefs,
                            settingsPrefs,
                            skin
                        ) {
                            it.padTop(30f).row()
                        }

                    }

                    textButton(text = bundle[PausePopUpBundle.BTBACK.bundle], style = Buttons.DEFAULT.skinKey) {
                        MenuView.attachTextMovement(this)
                        onClick { stage.fire(HideStoreEvent()) }
                        it.padTop(30f)
                    }
                }
                it.pad(10f)
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
fun <S> KWidget<S>.storeView(
    bundle: I18NBundle,
    playerPrefs: Preferences,
    settingsPref: Preferences,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: StoreView.(S) -> Unit = {}
): StoreView = actor(StoreView(skin, playerPrefs, settingsPref, bundle), init)