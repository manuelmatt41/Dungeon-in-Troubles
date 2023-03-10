package com.github.manu.dungeonintroubles.ui.view

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.I18NBundle
import com.github.manu.dungeonintroubles.event.PausePopUpEvent
import com.github.manu.dungeonintroubles.extension.fire
import com.github.manu.dungeonintroubles.ui.Drawables
import com.github.manu.dungeonintroubles.ui.get
import com.github.manu.dungeonintroubles.ui.model.GameModel
import com.github.manu.dungeonintroubles.ui.widget.*
import ktx.actors.alpha
import ktx.actors.onClick
import ktx.actors.plusAssign
import ktx.log.logger
import ktx.scene2d.*

/**
 * Vista que representa la interfaz del juego, se enseña informacion de la partida actual, menu de pausa o de menu de muerte
 *
 * @param model Modelo de la vista que recibe y notifica los cambios
 * @property bundle Conjunto de cadenas que pueden ser traducidas
 * @property playerPrefs Datos guardados del jugador del juego
 * @param settingsPrefs Datos guardados de los ajustes del juego
 * @param skin Skin de los componentes
 */
class GameView(
    model: GameModel,
    val bundle: I18NBundle,
    val playerPrefs: Preferences,
    settingsPrefs: Preferences,
    skin: Skin
) : Table(skin), KTable {

    /**
     * Componente que muestra informacion del personaje
     */
    var playerInfo: PlayerInfo

    /**
     * Imagen para pausar el juego
     */
    var pause: Image

    /**
     * Componente que es el menu de pausa del juego
     */
    lateinit var pausePopup: PausePopUp

    /**
     * Inicia la vista y sus componentes
     */
    init {
        // UI
        setFillParent(true)

        this@GameView.playerInfo = playerInfo(
            if (settingsPrefs.getString("selectedSkin") == "") Drawables.DEFAULT else enumValueOf(settingsPrefs.getString("selectedSkin").uppercase()),
            skin
        ) {
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
        pausePopup = pausePopUp(skin, bundle) {
            it.expand().center()

        }
        this += pausePopup
    }

    fun resume() {
        this.clear()
        this += playerInfo
        this += pause
    }

    fun death() {
        this.clear()
        val distance = playerInfo.labelDistance.text.toString()
        this += deathPopUp(
            skin,
            bundle,
            playerPrefs,
            distance.substring(0, distance.length - 1).replace(',', '.').toFloat(),
            playerInfo.labelCoins.text.toString().toInt()
        ) {
            it.expand().center()
        }
    }

    companion object {
        private val log = logger<GameView>()
    }
}

/**
 * Extension que hace de contructor de la vista al crearla directamente a un actor para los escenarios
 */
@Scene2dDsl
fun <S> KWidget<S>.gameView(
    model: GameModel,
    bundle: I18NBundle,
    playerPrefs: Preferences,
    setiingsPrefs: Preferences,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: GameView.(S) -> Unit = {}
): GameView = actor(GameView(model, bundle, playerPrefs, setiingsPrefs,skin), init)