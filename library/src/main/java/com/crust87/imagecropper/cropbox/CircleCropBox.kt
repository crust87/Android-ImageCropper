package com.crust87.imagecropper.cropbox

import android.graphics.Canvas
import android.graphics.RectF
import android.view.MotionEvent

class CircleCropBox(minSize: Float, touchSlop: Float,  bound: RectF, boxColor: Int, lineWidth: Int, anchorSize: Int)
    : CropBox(minSize, touchSlop, bound, boxColor, lineWidth) {

    val anchor = Anchor(0, anchorSize / 2f, touchSlop)

    var radius: Float
        get() = width / 2
        set(value) {
            val deltaRadius = radius - value
            width = value * 2
            height = width

            x += deltaRadius
            y += deltaRadius
        }

    var centerX: Float
        get() = x + radius
        set(value) {
            x = value - radius
        }

    var centerY: Float
        get() = y + radius
        set(value) {
            y = value - radius
        }

    private val locationX = Math.cos(45 * Math.PI / 180).toFloat()
    private val locationY = Math.sin(45 * Math.PI / 180).toFloat()

    init {
        centerX = bound.centerX() - bound.left
        centerY = bound.centerY() - bound.top
        setAnchor()
    }

    override fun processTouchEvent(event: MotionEvent): Boolean {
        val eventX = event.x - bound.left
        val eventY = event.y - bound.top
        if (event.action == MotionEvent.ACTION_DOWN) {
            postX = eventX
            postY = eventY

            currentEvent = when (anchor.contains(eventX, eventY)) {
                true -> Action.Resize
                false -> when (contains(eventX, eventY)) {
                    true -> Action.Move
                    false -> Action.None
                }
            }
            return false
        } else if (event.action == MotionEvent.ACTION_MOVE) {
            val dx = postX - eventX
            val dy = postY - eventY

            if (currentEvent == Action.Move) {
                move(dx, dy)
            } else if (currentEvent == Action.Resize) {
                scale(dx)
            }

            setAnchor()

            postX = eventX
            postY = eventY

            return true
        } else if (event.action == MotionEvent.ACTION_UP) {
            currentEvent = Action.None

            return false
        }

        return false
    }

    fun scale(d: Float): Boolean {
        val dRadius = radius - d

        if (dRadius > minSize / 2) {
            val left = centerX - dRadius > bound.left - bound.left
            val top = centerY - dRadius > bound.top - bound.top
            val right = centerX + dRadius < bound.right - bound.left
            val bottom = centerY + dRadius < bound.bottom - bound.top

            val lLeftNot = centerX + d - dRadius > bound.left - bound.left
            val lTopNot = centerY + d - dRadius > bound.top - bound.top
            val lRightNot = centerX - d + dRadius < bound.right - bound.left
            val lBottomNot = centerY - d + dRadius < bound.bottom - bound.top

            if (left && top && right && bottom) {
                radius = dRadius
            } else if (!left && top && right && bottom) {
                // Left
                if (lRightNot) {
                    radius = dRadius
                    centerX -= d
                }
            } else if (!left && !top && right && bottom) {
                // Left & Top
                if (lRightNot && lBottomNot) {
                    radius = dRadius
                    centerX -= d
                    centerY -= d
                }
            } else if (left && !top && right && bottom) {
                // Top
                if (lBottomNot) {
                    radius = dRadius
                    centerY -= d
                }
            } else if (left && !top && !right && bottom) {
                // Top & Right
                if (lBottomNot && lLeftNot) {
                    radius = dRadius
                    centerX += d
                    centerY -= d
                }
            } else if (left && top && !right && bottom) {
                // Right
                if (lLeftNot) {
                    radius = dRadius
                    centerX += d
                }
            } else if (left && top && !right && !bottom) {
                // Right & Bottom
                if (lLeftNot && lTopNot) {
                    radius = dRadius
                    centerX += d
                    centerY += d
                }
            } else if (left && top && right && !bottom) {
                // Bottom
                if (lTopNot) {
                    radius = dRadius
                    centerY += d
                }
            } else if (!left && top && right && !bottom) {
                // Left & Bottom
                if (lRightNot && lTopNot) {
                    radius = dRadius
                    centerX -= d
                    centerY += d
                }
            }
        } else {
            radius = minSize / 2
        }

        return true
    }

    override fun draw(canvas: Canvas) {
        maskCanvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), backgroundPaint)
        maskCanvas.drawCircle(centerX, centerY, radius, holePaint)
        canvas.drawBitmap(maskBitmap, null, bound, maskPaint)

        canvas.translate(bound.left, bound.top)
        canvas.drawCircle(centerX, centerY, radius, paint)
        if (currentEvent != Action.Move) {
            anchor.draw(canvas, anchorPaint)
        }
    }

    fun setAnchor() {
        anchor.setLocation(centerX + locationX * (radius), centerY - locationY * (radius))
    }
}