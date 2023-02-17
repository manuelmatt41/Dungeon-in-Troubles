package com.github.manu.dungeonintroubles.ui.widget

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Scaling
import com.github.manu.dungeonintroubles.ui.Drawables
import com.github.manu.dungeonintroubles.ui.Labels
import com.github.manu.dungeonintroubles.ui.get
import ktx.actors.plusAssign
import ktx.log.logger
import ktx.scene2d.*

@Scene2dDsl
class PlayerInfo(
    charDrawables: Drawables?,
    private val skin: Skin,
) : WidgetGroup(), KGroup {
    private val background: Image = Image(skin[Drawables.PLAYER_INFO])
    private val playerBgd: Image = Image(if (charDrawables == null) null else skin[Drawables.PLAYER])
    val labelDistance: Label
    val labelCoins: Label

    init {
        this += background
        this += playerBgd.apply {
            setPosition(2f, 2f)
            setSize(22f, 20f)
            setScaling(Scaling.contain)
        }

        labelDistance = label("", Labels.DEFAULT.skinKey).apply {
            setPosition(28f, 14f)
        }

        labelCoins = label("", Labels.DEFAULT.skinKey).apply {
            setPosition(35f, 3f)
        }

        this += labelDistance
        this += labelCoins

        touchable = Touchable.disabled
    }

    override fun getPrefWidth() = background.drawable.minWidth

    override fun getPrefHeight() = background.drawable.minHeight

    fun getCoin(coins: Int) = labelCoins.setText(coins.toString())


    fun move(distance: Float) = labelDistance.setText(String.format("%.2f m", distance))
    companion object {
        private val log = logger<PlayerInfo>()
    }
}

@Scene2dDsl
fun <S> KWidget<S>.playerInfo(
    charDrawable: Drawables?,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: PlayerInfo.(S) -> Unit = {}
): PlayerInfo = actor(PlayerInfo(charDrawable, skin), init)