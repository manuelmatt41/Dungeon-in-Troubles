package com.github.manu.dungeonintroubles.ui.view

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.I18NBundle
import com.github.manu.dungeonintroubles.component.PlayerComponent
import com.github.manu.dungeonintroubles.event.GameResumeEvent
import com.github.manu.dungeonintroubles.event.SetMenuScreenEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.manu.dungeonintroubles.ui.Buttons
import com.github.manu.dungeonintroubles.ui.Drawables
import com.github.manu.dungeonintroubles.ui.Labels
import com.github.manu.dungeonintroubles.ui.get
import com.github.manu.dungeonintroubles.ui.model.GameModel
import com.github.manu.dungeonintroubles.ui.widget.PlayerInfo
import com.github.manu.dungeonintroubles.ui.widget.playerInfo
import ktx.actors.alpha
import ktx.actors.onClick
import ktx.log.logger
import ktx.scene2d.*

class GameView(
    model: GameModel,
    bundle: I18NBundle,
    skin: Skin
) : Table(skin), KTable {

    private val playerInfo: PlayerInfo
    val table: Table

    init {
        // UI
        setFillParent(true)
        playerInfo = playerInfo(Drawables.PLAYER, skin) {
            this.alpha = 1f
            it.expand().top().left().row()
        }


        table = table {
            background = skin[Drawables.BACKGROUND_DISTANCE]

            textButton(text = bundle[MenuViewBundle.BTNEWGAME.bundle], style = Buttons.DEFAULT.skinKey) {
                onClick { stage.fire(GameResumeEvent()) }
                it.padBottom(10f)
                it.center().row()
            }

            textButton(text = bundle[MenuViewBundle.BTSETTING.bundle], style = Buttons.DEFAULT.skinKey) {

                it.padBottom(10f)
                it.center().row()
            }

            textButton(text = bundle[MenuViewBundle.BTEXIT.bundle], style = Buttons.DEFAULT.skinKey) {
                onClick {
                    stage.fire(SetMenuScreenEvent(PlayerComponent(model.playerCoins, model.playerDistance)))
                }
                it.padBottom(10f)
                it.center().row()
            }
            it.expand().width(160f).height(90f).center().top()
            this.alpha = 0f
        }

        model.onPropertyChange(GameModel::playerCoins) { coins ->
            log.debug { "Property coin changed" }
            playerInfo.getCoin(coins)
        }

        model.onPropertyChange(GameModel::playerDistance) { distance ->
//            log.debug { "Property coin changed" }
            playerInfo.move(distance)
        }
    }

    companion object {
        private val log = logger<GameView>()
    }
}

@Scene2dDsl
fun <S> KWidget<S>.gameView(
    model: GameModel,
    bundle: I18NBundle,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: GameView.(S) -> Unit = {}
): GameView = actor(GameView(model, bundle, skin), init)