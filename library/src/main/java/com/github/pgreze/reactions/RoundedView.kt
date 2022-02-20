package com.github.pgreze.reactions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View

/**
 * Reaction selector floating dialog background.
 */
@SuppressLint("ViewConstructor")
class RoundedView(context: Context, private val config: ReactionsConfig) : View(context) {

    private val paint = Paint().apply {
        color = config.popupColor
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val rect = RectF()

    override fun onDraw(canvas: Canvas) {
        // Draw the background rounded rectangle
        paint.setShadowLayer(5.0f, 0.0f, 0.0f, 0x55000000)

        rect.left = 8f
        rect.right = width.toFloat() - 8
        rect.top = 8f
        rect.bottom = height.toFloat() - 8
        val popupCornerRadius = config.popupCornerRadius.toFloat()
        canvas.drawRoundRect(rect, popupCornerRadius, popupCornerRadius, paint)
    }
}
