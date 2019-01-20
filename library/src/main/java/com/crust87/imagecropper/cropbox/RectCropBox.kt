package com.crust87.imagecropper.cropbox

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.MotionEvent

class RectCropBox(context: Context, leftMargin: Float, topMargin: Float, bound: Rect, boxColor: Int, lineWidth: Int, anchorSize: Int)
    : CropBox(context, leftMargin, topMargin, bound, boxColor, lineWidth) {

    companion object {
        const val TOP_LEFT = 0
        const val TOP_RIGHT = 1
        const val BOTTOM_LEFT = 2
        const val BOTTOM_RIGHT = 3
        val ANCHOR_LIST = arrayListOf(TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT)
    }

    val anchors = ANCHOR_LIST.map { id ->
        Anchor(context, id, anchorSize / 2f).apply {
            setColor(boxColor)
        }
    }

    init {
        x = bound.exactCenterX() - width / 2 - leftMargin
        y = bound.exactCenterY() - height / 2 - topMargin

        setAnchor()
    }

    override fun processTouchEvent(event: MotionEvent): Boolean {
        val eventX = event.x - leftMargin
        val eventY = event.y - topMargin

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                postX = eventX
                postY = eventY

                for (anchor in anchors) {
                    if (anchor.contains(eventX, eventY)) {
                        currentEvent = Action.Resize
                        currentAnchor = anchor.id

                        return false
                    }
                }

                currentEvent = when (contains(eventX, eventY)) {
                    true -> Action.Move
                    false -> Action.None
                }

                return false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = postX - eventX
                val dy = postY - eventY

                when (currentEvent) {
                    Action.Move -> move(dx, dy)
                    Action.Resize -> resize(dx, dy)
                }

                setAnchor()

                postX = eventX
                postY = eventY

                return true
            }

            MotionEvent.ACTION_UP -> {
                currentEvent = Action.None

                return false
            }
        }

        return false
    }
    
    fun resize(dx: Float, dy: Float): Boolean {
        if (currentAnchor != -1) {
            var deltaX = x
            var deltaY = y
            var deltaWidth = width
            var deltaHeight = height

            when (currentAnchor) {
                TOP_LEFT -> {
                    deltaX -= dx
                    deltaY -= dy
                    deltaWidth += dx
                    deltaHeight += dy
                }
                TOP_RIGHT -> {
                    deltaY -= dy
                    deltaWidth -= dx
                    deltaHeight += dy
                }
                BOTTOM_LEFT -> {
                    deltaX -= dx
                    deltaWidth += dx
                    deltaHeight -= dy
                }
                BOTTOM_RIGHT -> {
                    deltaWidth -= dx
                    deltaHeight -= dy
                }
            }

            if (deltaX < bound.left - leftMargin) {
                deltaX = bound.left - leftMargin
                deltaWidth = (x + width) - deltaX
            }

            if (deltaX + deltaWidth > bound.right - leftMargin) {
                deltaWidth = bound.right - leftMargin - deltaX
            }

            if (deltaY < bound.top - topMargin) {
                deltaY = bound.top - topMargin
                deltaHeight = (y + height) - deltaY
            }

            if (deltaY + deltaHeight > bound.bottom - topMargin) {
                deltaHeight = bound.bottom - topMargin - deltaY
            }

            if (deltaWidth < minSize) {
                if (currentAnchor == TOP_LEFT || currentAnchor == BOTTOM_LEFT) {
                    deltaX = (x + width) - minSize
                }

                deltaWidth = minSize
            }

            if (deltaHeight < minSize) {
                if (currentAnchor == TOP_LEFT || currentAnchor == TOP_RIGHT) {
                    deltaY = (y + height) - minSize
                }

                deltaHeight = minSize
            }

            x = deltaX
            y = deltaY
            width = deltaWidth
            height = deltaHeight

            return true
        }

        return false
    }

    override fun draw(canvas: Canvas) {
        maskCanvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), backgroundPaint)
        maskCanvas.drawRect(x, y, x + width, y + height, holePaint)
        canvas.drawBitmap(maskBitmap, null, bound, maskPaint)

        canvas.translate(leftMargin, topMargin)
        canvas.drawRect(x, y, x + width, y + height, paint)
        if (currentEvent != Action.Move) {
            anchors.map { anchor ->
                anchor.draw(canvas)
            }
        }
    }

    fun setAnchor() = anchors.map { anchor ->
        when (anchor.id) {
            TOP_LEFT -> anchor.setLocation(x, y)
            TOP_RIGHT -> anchor.setLocation(x + width, y)
            BOTTOM_LEFT -> anchor.setLocation(x, y + height)
            BOTTOM_RIGHT -> anchor.setLocation(x + width, y + height);
        }
    }
}