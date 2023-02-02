package com.github.manu.dungeonintroubles.ui.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.github.manu.dungeonintroubles.ui.Drawables
import com.github.manu.dungeonintroubles.ui.widget.PlayerInfo
import com.github.manu.dungeonintroubles.ui.widget.playerInfo
import ktx.actors.alpha
import ktx.scene2d.*

class GameView(
    skin: Skin
) : Table(skin), KTable {

    private val playerInfo: PlayerInfo

    init {
        // UI
        setFillParent(true)

        playerInfo = playerInfo(Drawables.PLAYER, skin) {
            this.alpha = 1f
            it.expand().top().left()
        }
    }

}

@Scene2dDsl
fun <S> KWidget<S>.gameView(
    skin: Skin = Scene2DSkin.defaultSkin,
    init: GameView.(S) -> Unit = {}
): GameView = actor(GameView(skin), init)