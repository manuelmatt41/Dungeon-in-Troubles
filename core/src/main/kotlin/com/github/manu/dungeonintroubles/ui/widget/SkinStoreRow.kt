package com.github.manu.dungeonintroubles.ui.widget

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.I18NBundle
import com.github.manu.dungeonintroubles.event.HideStoreEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.manu.dungeonintroubles.ui.Buttons
import com.github.manu.dungeonintroubles.ui.Drawables
import com.github.manu.dungeonintroubles.ui.Labels
import com.github.manu.dungeonintroubles.ui.get
import com.github.manu.dungeonintroubles.ui.view.MenuView
import com.github.manu.dungeonintroubles.ui.view.PlayerSkins
import com.github.manu.dungeonintroubles.ui.view.settingsView
import ktx.actors.onClick
import ktx.actors.plusAssign
import ktx.log.logger
import ktx.scene2d.*
/**
 * Enumerado para llamar al bundle de idiomas de forma mas legible
 */
enum class SkinStoreRowBundle {
    PURCHASED;
    /**
     * Convierte el enumerado en la Key del bundle
     */
    var bundle: String = "SkinStoreRowBundle.${this.toString().lowercase()}"
}

/**
 * Clase que representa un componente de la UI, que es a fila de la tienda para comprar skins
 *
 * @param charDrawables Imagen que representa a skin que se puede comprar
 * @param name Nombre de la skin
 * @param isPurchased Valor para comprobar si esta comprada la skin
 * @param bundle Conjunto de cadenas que pueden ser traducidas
 * @param playerPrefs Datos guardados del jugador del juego
 * @param settingsPrefs Datos guardados de los ajustes del juego
 * @param skin Skin de los componentes
 */
class SkinStoreRow(
    charDrawables: Drawables,
    name: PlayerSkins,
    isPurchased: Boolean,
    bundle: I18NBundle,
    playerPrefs: Preferences,
    settingsPrefs: Preferences,
    private val skin: Skin,
) : WidgetGroup(), KGroup {

    /**
     * Tabla que contiene los componentes
     */
    private val table: Table

    /**
     * Boton para poder comprar
     */
    var button: TextButton

    /**
     * Inicia la clase y sus componentes
     */
    init {
        table = table {

            image(skin[charDrawables]) {
                it.width(25f).height(35f)
            }

            label(text = name.name, style = Labels.DEFAULT.skinKey) {
                it.width(125f).height(30f).padLeft(10f).padTop(20f)
            }

            this@SkinStoreRow.button = textButton(
                text = if (isPurchased) bundle[SkinStoreRowBundle.PURCHASED.bundle] else "Coin: ${name.coin}",
                style = Buttons.DEFAULT.skinKey
            ) {
                MenuView.attachTextMovement(this)
                if (!isPurchased) {
                    label.color = if (playerPrefs.getInteger("coins") >= name.coin) Color.GREEN else Color.RED
                }
                if (settingsPrefs.getString("selectedSkin") == name.atlasKey) {
                    color = Color.GRAY
                }
                if (isPurchased) {
                    onClick {
                        settingsPrefs.putString("selectedSkin", name.atlasKey)
                        settingsPrefs.flush()
                        stage.fire(HideStoreEvent())
                    }
                } else {
                    onClick {
                        if (playerPrefs.getInteger("coins") >= name.coin) {
                            settingsPrefs.putBoolean("skin/${name.name}", true)
                            settingsPrefs.flush()
                            playerPrefs.putInteger("coins", playerPrefs.getInteger("coins") - name.coin)
                            playerPrefs.flush()
                            stage.fire(HideStoreEvent())
                        }
                    }
                }
                it.width(50f).height(30f)
            }
        }

        this += table

        setPosition(100f, 100f)
    }

    companion object {
        private val log = logger<SkinStoreRowBundle>()
    }
}

/**
 * Extension que hace de contructor de la vista al crearla directamente a un actor para los escenarios
 */
@Scene2dDsl
fun <S> KWidget<S>.skinStoreRow(
    charDrawable: Drawables,
    name: PlayerSkins,
    isPurchased: Boolean,
    bundle: I18NBundle,
    playerPrefs: Preferences,
    settingsPrefs: Preferences,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: SkinStoreRow.(S) -> Unit = {}
): SkinStoreRow = actor(SkinStoreRow(charDrawable, name, isPurchased, bundle, playerPrefs, settingsPrefs, skin), init)