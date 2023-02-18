package com.github.manu.dungeonintroubles.actor

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable

/**
 * Clase que permite girar la imagen en el eje x
 */
class FlipImage : Image() {
    /**
     * Valor para dibujar la imagen invertida si esta en true
     */
    var flipX = false

    /**
     * Se encarga de dibujar en pantalla la imagen dependiendo si esta invertida o no
     *
     * @param batch Se encarga de representar las imagenes por GPU
     * @param parentAlpha Valor de la transparencia de la imagen que se va a representar
     */
    override fun draw(batch: Batch, parentAlpha: Float) {
        validate()

        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)

        val toDraw = drawable
        if (toDraw is TransformDrawable && (scaleX != 1f || scaleY != 1f || rotation != 0f)) {
            toDraw.draw(
                batch,
                if (flipX) x + imageX + imageWidth * scaleX else x + imageX,
                y + imageY,
                originX - imageX,
                originY - imageY,
                imageWidth,
                imageHeight,
                if (flipX) -scaleX else scaleX,
                scaleY,
                rotation
            )
        } else {
            toDraw?.draw(
                batch,
                if (flipX) x + imageX + imageWidth * scaleX else x + imageX,
                y + imageY,
                if (flipX) -imageWidth * scaleX else imageWidth * scaleX,
                imageHeight * scaleY
            )
        }
    }
}