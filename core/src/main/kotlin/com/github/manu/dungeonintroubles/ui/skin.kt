package com.github.manu.dungeonintroubles.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import ktx.assets.disposeSafely
import ktx.scene2d.Scene2DSkin
import ktx.style.*

/**
 * Enumerado que representa las imagenes que se pueden usar
 *
 * @param atlasKey Valor de la key para el texture atlas
 */
enum class Drawables(
    val atlasKey: String
) {
    DEFAULT("default"),
    ELF_F("elf_f"),
    ELF_M("elf_m"),
    KNIGHT_F("knight_f"),
    KNIGHT_M("knight_m"),
    LIZARD("lizard"),
    WITCH("witch"),
    WITCHER("witcher"),
    PUMPKIN_DUDE("pumpkin_dude"),
    DOCTOR("doctor"),
    TRAP("trap"),
    FIREBALL("fireball"),
    COIN("coin"),
    DEMON("demon"),
    PORTAL("portal"),
    PLAYER_INFO("playerinfo"),
    BACKGROUND_COIN("bgdCoin"),
    BACKGROUND_DISTANCE("bgdDistance"),
    BUTTON_UNPRESSED("button_unpressed"),
    BUTTON_PRESSED("button_pressed"),
    BUTTON_DISSAPEAR("button_dissapear"),
    PAUSE_POPUP_BACKGROUND("pausepopupbgd"),
    DEATH_POPUP_BACKGROUND("deathpopupbgd"),
    KNOB_UNPRESSED("knob_unpressed"),
    KNOB_PRESSED("knob_pressed"),
    CH_CHECKED("ch_checked"),
    CH_UNCHECKED("ch_unchecked"),
    BTPAUSE("btpause");
}

/**
 * Funcion para transformar el enumerado en un Drawable a traves de la skin
 */
operator fun Skin.get(drawable: Drawables): Drawable = this.getDrawable(drawable.atlasKey)

/**
 * Enumerado que representa los estilos de las etiquetas
 */
enum class Labels {
    DEFAULT, TITLE;

    /**
     * Nombre que se lleva el estilo de la etiqueta
     */
    val skinKey = this.name.lowercase()
}

/**
 * Enumerado que representa los estilos de los botones
 */
enum class Buttons {
    DEFAULT;

    /**
     * Nombre que se lleva el estilo del boton
     */
    val skinKey = this.name.lowercase()
}

/**
 * Enumerado que representa los estilos de los paneles deslizantes
 */
enum class ScrollPanes {
    DEFAULT;

    /**
     * Nombre que se lleva el estilo del panel
     */
    val skinKey = this.name.lowercase()
}

/**
 * Enumerado que representa los estilos de los deslizadores
 */
enum class Sliders {
    DEFAULT;

    /**
     * Nombre que se lleva el estilo del deslizador
     */
    val skinKey = this.name.lowercase()
}

/**
 * Enumerado que representa los estilos de las casillas
 */
enum class CheckBoxes {
    DEFAULT;

    /**
     * Nombre que se lleva el estilo de la casilla
     */
    val skinKey = this.name.lowercase()
}

/**
 * Enumerado que representa las fuentes que se pueden usar
 *
 * @param atlasRegionKey Valor de la key para el texture atlas
 * @param scaling Escalado del tamaño de la fuente
 */
enum class Fonts(
    val atlasRegionKey: String,
    val scaling: Float,
) {
    DEFAULT("minimalpixel", 1f),
    TITLE("minimalpixel", 2.2f);

    /**
     * Nombre que se lleva el estilo de la fuente
     */
    val skinKey = "Font_${this.name.lowercase()}"

    /**
     * Pat del archivo de la fuente
     */
    val fontPath = "ui/${this.atlasRegionKey}.fnt"
}

/**
 * Funcion para transformar el enumerado en un BitmapFont a traves de la skin
 */
operator fun Skin.get(font: Fonts): BitmapFont = this.getFont(font.skinKey)

fun loadSkin() {
    //Carga los archivos de las fuentes
    Scene2DSkin.defaultSkin = skin(TextureAtlas(Gdx.files.internal("ui/ui.atlas"))) { skin ->
        Fonts.values().forEach { fnt ->
            skin[fnt.skinKey] = BitmapFont(Gdx.files.internal(fnt.fontPath), skin.getRegion(fnt.atlasRegionKey)).apply {
                data.setScale(fnt.scaling)
                data.markupEnabled = true
            }
        }
        //Crea todos lo estilos
        label(Labels.DEFAULT.skinKey) {
            font = skin[Fonts.DEFAULT]
        }

        label(Labels.TITLE.skinKey) {
            font = skin[Fonts.TITLE]
        }

        textButton(Buttons.DEFAULT.skinKey) {
            font = skin[Fonts.DEFAULT]
            up = skin[Drawables.BUTTON_UNPRESSED]
            down = skin[Drawables.BUTTON_PRESSED]
        }

        scrollPane(ScrollPanes.DEFAULT.skinKey) {
        }

        slider(Sliders.DEFAULT.skinKey) {
            background = skin[Drawables.BACKGROUND_DISTANCE]
            knob = skin[Drawables.KNOB_UNPRESSED]
            knobDown = skin[Drawables.KNOB_PRESSED]

        }

        checkBox(CheckBoxes.DEFAULT.skinKey) {
            font = skin[Fonts.DEFAULT]
            checkboxOn = skin[Drawables.CH_CHECKED]
            checkboxOff = skin[Drawables.CH_UNCHECKED]
        }
    }

}

/**
 * Librera lo recursos que ocupa la skin para la UI
 */
fun disposeSkin() {
    Scene2DSkin.defaultSkin.disposeSafely()
}