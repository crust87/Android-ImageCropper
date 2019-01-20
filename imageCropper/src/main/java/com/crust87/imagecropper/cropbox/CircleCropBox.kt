package com.crust87.imagecropper.cropbox

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.MotionEvent

class CircleCropBox(context: Context, leftMargin: Float, topMargin: Float, bound: Rect, boxColor: Int, lineWidth: Int, anchorSize: Int)
    : CropBox(context, leftMargin, topMargin, bound, boxColor, lineWidth) {

    val anchor = Anchor(0, anchorSize / 2f).apply {
        setColor(boxColor)
    }

    private val locationX = Math.cos(45 * Math.PI / 180).toFloat()
    private val locationY = Math.sin(45 * Math.PI / 180).toFloat()

    init {
        setAnchor()
    }

    override fun processTouchEvent(event: MotionEvent): Boolean {
        return false
    }

    override fun draw(canvas: Canvas) {
        maskCanvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), backgroundPaint)
        maskCanvas.drawCircle(x + width / 2, y + width / 2, width / 2, holePaint)
        canvas.drawBitmap(maskBitmap, null, bound, maskPaint)

        canvas.translate(leftMargin, topMargin)
        canvas.drawCircle(x + width / 2, y + width / 2, width / 2, paint);
        if (currentEvent != Action.Move) {
            anchor.draw(canvas)
        }
    }

    fun setAnchor() {
        anchor.setLocation((x + width / 2) + locationX * (width / 2), (y + width / 2) - locationY * (width / 2));
    }
}