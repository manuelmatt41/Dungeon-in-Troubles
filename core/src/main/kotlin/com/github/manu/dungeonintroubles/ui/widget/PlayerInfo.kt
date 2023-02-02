package com.github.manu.dungeonintroubles.ui.widget

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Scaling
import com.github.manu.dungeonintroubles.ui.Drawables
import com.github.manu.dungeonintroubles.ui.Labels
import com.github.manu.dungeonintroubles.ui.get
import ktx.actors.plusAssign
import ktx.scene2d.*

@Scene2dDsl
class PlayerInfo(
    charDrawables: Drawables?,
    private val skin: Skin,
) : WidgetGroup(), KGroup {
    private val background: Image = Image(skin[Drawables.PLAYER_INFO])
    private val playerBgd: Image = Image(if (charDrawables == null) null else skin[Drawables.PLAYER])

    init {
        this += background
        this += playerBgd.apply {
            setPosition(2f, 2f)
            setSize(22f, 20f)
            setScaling(Scaling.contain)
        }

        this += label("Prueba metros", Labels.DISTANCE.skinKey).apply {
            setPosition(28f, 13f)
        }

        this += label("Prueba coin", Labels.COIN.skinKey).apply {
            setPosition(35f, 2f)
        }
    }

    override fun getPrefWidth() = background.drawable.minWidth

    override fun getPrefHeight() = background.drawable.minHeight

    fun character(charDrawable: Drawables?) {
        if (charDrawable == null) {
            playerBgd.drawable = null
        } else {
            playerBgd.drawable = skin[charDrawable]
        }
    }
}

@Scene2dDsl
fun <S> KWidget<S>.playerInfo(
    charDrawable: Drawables?,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: PlayerInfo.(S) -> Unit = {}
): PlayerInfo = actor(PlayerInfo(charDrawable, skin), init)