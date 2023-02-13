package com.github.manu.dungeonintroubles.ui.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.github.manu.dungeonintroubles.ui.widget.GameMenu
import com.github.manu.dungeonintroubles.ui.widget.gameMenu
import ktx.actors.alpha
import ktx.scene2d.*

class MenuView(
    skin: Skin
) : Table(), KTable {

    private val gameMenu: GameMenu

    init {
        setFillParent(true)

        gameMenu = gameMenu(skin) {
            this.alpha = 1f
            it.expand().fill()
        }
    }
}

@Scene2dDsl
fun <S> KWidget<S>.menuView(
    skin: Skin = Scene2DSkin.defaultSkin,
    init: MenuView.(S) -> Unit = {}
): MenuView = actor(MenuView(skin), init)