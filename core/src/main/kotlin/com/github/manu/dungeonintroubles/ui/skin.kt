package com.github.manu.dungeonintroubles.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import ktx.assets.disposeSafely
import ktx.scene2d.Scene2DSkin
import ktx.style.*

enum class Drawables(
    val atlasKey: String
) {
    PLAYER("player"),
    PLAYER_INFO("playerinfo"),
    BACKGROUND_COIN("bgdCoin"),
    BACKGROUND_DISTANCE("bgdDistance"),
    BUTTON_UNPRESSED("button_unpressed"),
    BUTTON_PRESSED("button_pressed"),
    BUTTON_DISSAPEAR("button_dissapear")
}
operator fun Skin.get(drawable: Drawables): Drawable = this.getDrawable(drawable.atlasKey)

enum class Labels {
    DEFAULT;

    val skinKey = this.name.lowercase()
}

enum class Buttons {
    DEFAULT;

    val skinKey = this.name.lowercase()
}

enum class Fonts(
    val atlasRegionKey: String,
    val scaling: Float,
) {
    DEFAULT("minimalpixel", 0.75f);

    val skinKey = "Font_${this.name.lowercase()}"
    val fontPath = "ui/${this.atlasRegionKey}.fnt"
}

operator fun Skin.get(font: Fonts): BitmapFont = this.getFont(font.skinKey)
//operator fun Skin.get(font: Fonts): BitmapFont = this.getFont(font.skinKey)

fun loadSkin() {
    Scene2DSkin.defaultSkin = skin(TextureAtlas(Gdx.files.internal("ui/ui.atlas"))) { skin ->
        Fonts.values().forEach { fnt ->
            skin[fnt.skinKey] = BitmapFont(Gdx.files.internal(fnt.fontPath), skin.getRegion(fnt.atlasRegionKey)).apply {
                data.setScale(fnt.scaling)
                data.markupEnabled = true
            }
        }

        label(Labels.DEFAULT.skinKey) {
            font = skin[Fonts.DEFAULT]
            background = skin[Drawables.BACKGROUND_COIN].apply {
                leftWidth = 2f
                rightWidth = 2f
                topHeight = 4f
            }
        }

        textButton(Buttons.DEFAULT.skinKey) {
            font = skin[Fonts.DEFAULT]
            up = skin[Drawables.BUTTON_UNPRESSED]
            down = skin[Drawables.BUTTON_PRESSED]
        }
    }

}

fun disposeSkin() {
    Scene2DSkin.defaultSkin.disposeSafely()
}