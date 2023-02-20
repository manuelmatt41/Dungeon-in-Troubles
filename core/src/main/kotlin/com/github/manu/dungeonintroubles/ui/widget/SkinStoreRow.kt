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

enum class SkinStoreRowBundle {
    PURCHASED;

    var bundle: String = "SkinStoreRowBundle.${this.toString().lowercase()}"
}

class SkinStoreRow(
    charDrawables: Drawables,
    name: PlayerSkins,
    isPurchased: Boolean,
    bundle: I18NBundle,
    playerPrefs: Preferences,
    settingsPrefs: Preferences,
    private val skin: Skin,
) : WidgetGroup(), KGroup {

    private val table: Table
    var button: TextButton

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