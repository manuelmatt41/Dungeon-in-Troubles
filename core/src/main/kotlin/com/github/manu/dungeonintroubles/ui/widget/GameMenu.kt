package com.github.manu.dungeonintroubles.ui.widget

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.github.manu.dungeonintroubles.ui.Drawables
import com.github.manu.dungeonintroubles.ui.get
import ktx.actors.plusAssign
import ktx.scene2d.*

@Scene2dDsl
class GameMenu(
    private val skin: Skin,
) : WidgetGroup(), KGroup {

    private val background: Image = Image(skin[Drawables.BACKGROUND_DISTANCE])
    private val btNewGame: Button

    init {
        this += background.apply {
            setSize(Gdx.app.graphics.width.toFloat(), Gdx.app.graphics.height.toFloat())
        }

        btNewGame = Button(skin[Drawables.BUTTON_UNPRESSED], skin[Drawables.BUTTON_PRESSED], skin[Drawables.BUTTON_DISSAPEAR]).apply {
            setPosition(10f, 10f)
        }

        this += btNewGame
    }


    override fun getPrefWidth() = background.drawable.minWidth

    override fun getPrefHeight() = background.drawable.minHeight
}

@Scene2dDsl
fun <S> KWidget<S>.gameMenu(
    skin: Skin = Scene2DSkin.defaultSkin,
    init: GameMenu.(S) -> Unit = {}
): GameMenu = actor(GameMenu(skin), init)