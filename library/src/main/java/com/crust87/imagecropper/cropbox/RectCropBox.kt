package com.crust87.imagecropper.cropbox

import android.graphics.Canvas
import android.graphics.RectF
import android.view.MotionEvent

class RectCropBox(minSize: Float, touchSlop: Float, bound: RectF, boxColor: Int, lineWidth: Int, anchorSize: Int)
    : CropBox(minSize, touchSlop, bound, boxColor, lineWidth) {

    companion object {
        const val TOP_LEFT = 0
        const val TOP_RIGHT = 1
        const val BOTTOM_LEFT = 2
        const val BOTTOM_RIGHT = 3
        val ANCHOR_LIST = arrayListOf(TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT)
    }
    internal var currentAnchor = -1

    val anchors = ANCHOR_LIST.map { id ->
        Anchor(id, anchorSize / 2f, touchSlop)
    }

    init {
        x = bound.centerX() - width / 2 - bound.left
        y = bound.centerY() - height / 2 - bound.top

        setAnchor()
    }

    override fun processTouchEvent(event: MotionEvent): Boolean {
        val eventX = event.x - bound.left
        val eventY = event.y - bound.top

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

            if (deltaX < 0) {
                deltaX = 0f
                deltaWidth = (x + width) - deltaX
            }

            if (deltaX + deltaWidth > bound.width()) {
                deltaWidth = bound.width() - deltaX
            }

            if (deltaY < 0) {
                deltaY = 0f
                deltaHeight = (y + height) - deltaY
            }

            if (deltaY + deltaHeight > bound.height()) {
                deltaHeight = bound.height() - deltaY
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

        canvas.translate(bound.left, bound.top)
        canvas.drawRect(x, y, x + width, y + height, paint)
        if (currentEvent != Action.Move) {
            anchors.map { anchor ->
                anchor.draw(canvas, anchorPaint)
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