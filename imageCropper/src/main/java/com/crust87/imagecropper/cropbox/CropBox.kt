/*
 * ImageCropper
 * https://github.com/crust87/Android-ImageCropper
 *
 * Mabi
 * crust87@gmail.com
 * last modify 2015-05-19
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.crust87.imagecropper.cropbox

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import com.crust87.imagecropper.R

class CropBox(val context: Context, val leftMargin: Float, val topMargin: Float, val bound: Rect, val scale: Float, var boxColor: Int, val lineWidth: Int, val anchorSize: Int) {

    enum class Action {
        resize, move, none
    }

    val minSize = context.resources.getDimensionPixelSize(R.dimen.min_box_Size)

    val paint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = lineWidth.toFloat()
        color = boxColor
        strokeWidth = lineWidth.toFloat()
    }

    val maskPaint1 = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
    }

    val maskPaint2 = Paint().apply {
        color = Color.WHITE
        xfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)
    }

    val bitmapPaint = Paint().apply {
        isFilterBitmap = true
        alpha = 128
    }

    var bitmap: Bitmap
    var canvas: Canvas

    // Attributes
    var x = 0f
    var y = 0f
    var width = minSize
    var height = minSize
    var postX = 0f
    var postY = 0f

    // Working Variables
    var currentEvent : Action = Action.none

//    protected abstract val x: Float
//    protected abstract val y: Float
//    protected abstract val width: Float
//    protected abstract val height: Float
//
//    abstract val cropX: Int
//    abstract val cropY: Int
//    abstract val cropWidth: Int
//    abstract val cropHeight: Int

    init {
        paint.strokeWidth = lineWidth.toFloat()
        bitmap = Bitmap.createBitmap(bound.width(), bound.height(), Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
    }

    fun processTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                postX = x
                postY = y

                currentEvent = when (contains(event.x - leftMargin, event.y - topMargin)) {
                    true -> Action.move
                    false -> Action.none
                }

                return false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = postX - x
                val dy = postY - y

                var actionResult = false
                if(currentEvent == Action.move) {
                    actionResult = move(dx, dy)
                }

                if(actionResult) {
                    setAnchor()
                }

                postX = x
                postY = y

                return true
            }

            MotionEvent.ACTION_UP -> {
                currentEvent = Action.none

                return false
            }
        }

        return false
    }
    fun move(dx: Float, dy: Float): Boolean {
        var deltaX = dx
        var deltaY = dy

        val top = y - dy
        if (top < (bound.top - topMargin)) {
            deltaY = dy - (bound.top - topMargin) + top
        }

        val bottom = y + height - dy
        if (bottom > (bound.bottom - topMargin)) {
            deltaY = dy - (bound.bottom - topMargin) + bottom
        }

        val left = x - dx
        if (left < (bound.left - leftMargin)) {
            deltaX = dx - (bound.left - leftMargin) + left
        }

        val right = x + width - dx
        if (right > (bound.right - leftMargin)) {
            deltaX = dx - (bound.right - leftMargin) + right
        }

        x -= deltaX
        y -= deltaY

        return true
    }

    fun setAnchor() {

    }

    fun contains(ex: Float, ey: Float): Boolean {
        return ex >= x && ex <= x + width && ey >= y && ey <= y + height
    }

    fun draw(canvas: Canvas) {
        this.canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), maskPaint1);
        this.canvas.drawRect(x, y, x + width, y + height, maskPaint2);
        this.canvas.drawRect(x, y, x + width, y + height, paint);

        canvas.drawBitmap(bitmap, null, bound, bitmapPaint);

		if(currentEvent != Action.move) {
//			for(Anchor anchor: mAnchors) {
//				anchor.draw(canvas);
//			}
		}
    }

    override fun toString(): String {
        return "view x: $x, y: $y, width: 0, height: 0"
    }

    open fun setColor(color: Int) {
        boxColor = color
        paint.color = boxColor
    }

    class CropBoxBuilder {
        private var newBoxType: Int = 0
        private var newLeftMargin: Float = 0.toFloat()
        private var newTopMargin: Float = 0.toFloat()
        private var newBound: Rect = Rect()
        private var newScale: Float = 0.toFloat()
        private var newBoxColor: Int = 0
        private var newLineWidth: Int = 0
        private var newAnchorSize: Int = 0

        fun setBoxType(boxType: Int): CropBoxBuilder {
            newBoxType = boxType
            return this
        }

        fun setLeftMargin(leftMargin: Float): CropBoxBuilder {
            newLeftMargin = leftMargin
            return this
        }

        fun setTopMargin(topMargin: Float): CropBoxBuilder {
            newTopMargin = topMargin
            return this
        }

        fun setBound(bound: Rect): CropBoxBuilder {
            newBound = bound
            return this
        }

        fun setScale(scale: Float): CropBoxBuilder {
            newScale = scale
            return this
        }

        fun setBoxColor(boxColor: Int): CropBoxBuilder {
            newBoxColor = boxColor
            return this
        }

        fun setLineWidth(lineWidth: Int): CropBoxBuilder {
            newLineWidth = lineWidth
            return this
        }

        fun setAnchorSize(anchorSize: Int): CropBoxBuilder {
            newAnchorSize = anchorSize
            return this
        }

        fun createCropBox(context: Context): CropBox {
            return CropBox(context, newLeftMargin, newTopMargin, newBound, newScale, newBoxColor, newLineWidth, newAnchorSize)
        }
    }
}
