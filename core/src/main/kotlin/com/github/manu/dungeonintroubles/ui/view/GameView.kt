package com.github.manu.dungeonintroubles.ui.view

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.I18NBundle
import com.github.manu.dungeonintroubles.component.PlayerComponent
import com.github.manu.dungeonintroubles.event.GameResumeEvent
import com.github.manu.dungeonintroubles.event.PausePopUpEvent
import com.github.manu.dungeonintroubles.event.SetMenuScreenEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.manu.dungeonintroubles.ui.Buttons
import com.github.manu.dungeonintroubles.ui.Drawables
import com.github.manu.dungeonintroubles.ui.Labels
import com.github.manu.dungeonintroubles.ui.get
import com.github.manu.dungeonintroubles.ui.model.GameModel
import com.github.manu.dungeonintroubles.ui.widget.*
import ktx.actors.alpha
import ktx.actors.onClick
import ktx.actors.onTouchDown
import ktx.actors.plusAssign
import ktx.app.KtxInputAdapter
import ktx.log.logger
import ktx.scene2d.*
import javax.swing.event.PopupMenuEvent

class GameView(
    model: GameModel,
    val bundle: I18NBundle,
    val prefs: Preferences,
    skin: Skin
) : Table(skin), KTable {

    var playerInfo: PlayerInfo
    var pause: Image
    lateinit var pausePopup: PausePopUp
    lateinit var deathPopup: DeathPopUp

    init {
        // UI
        setFillParent(true)

        this@GameView.playerInfo = playerInfo(Drawables.PLAYER, skin) {
            this.alpha = 1f
            it.expand().padTop(2f).padLeft(2f).left().top()
        }

        pause = image(skin[Drawables.BTPAUSE]) {
            onClick {
                stage.fire(PausePopUpEvent())
            }

            it.padTop(3f).padRight(6f).right().top()
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

    fun pause() {
        this.clear()
        this += pausePopUp(skin, bundle) {
            it.expand().center()

        }
    }

    fun resume() {
        this.clear()
        this += playerInfo
        this += pause
    }

    fun death() {
        this.clear()
        var distance = playerInfo.labelDistance.text.toString()
        this += deathPopUp(
            skin,
            bundle,
            prefs,
            distance.substring(0, distance.length - 1).replace(',', '.').toFloat()
        ) {
            it.expand().center()
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
    prefs: Preferences,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: GameView.(S) -> Unit = {}
): GameView = actor(GameView(model, bundle, prefs, skin), init)