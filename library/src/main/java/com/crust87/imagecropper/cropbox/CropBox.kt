/*
 * ImageCropper
 * https://github.com/crust87/Android-ImageCropper
 *
 * Mabi
 * crust87@gmail.com
 * last modify 2019-01-19
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

import android.graphics.*
import android.view.MotionEvent
import com.crust87.imagecropper.ImageCropper.Companion.DEFAULT_BOX_TYPE
import com.crust87.imagecropper.ImageCropper.Companion.RECT_CROP_BOX

abstract class CropBox(val minSize: Float,
                       val touchSlop: Float,
                       val bound: RectF,
                       boxColor: Int,
                       lineWidth: Int) {

    enum class Action {
        Resize, Move, None
    }

    val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = lineWidth.toFloat()
        color = boxColor
    }

    val anchorPaint = Paint().apply {
        isAntiAlias = true
        color = boxColor
    }

    val backgroundPaint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
    }

    val holePaint = Paint().apply {
        color = Color.WHITE
        xfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)
    }

    val maskPaint = Paint().apply {
        isFilterBitmap = true
        alpha = 128
    }

    /*
    Attributes
     */
    var x = 0f
    var y = 0f
    var width = minSize
    var height = minSize

    /*
    Working Variables
     */
    internal var maskBitmap: Bitmap
    internal var maskCanvas: Canvas
    internal var postX = 0f
    internal var postY = 0f
    internal var currentEvent: Action = Action.None
    internal var currentAnchor = -1

    init {
        maskBitmap = Bitmap.createBitmap(bound.width().toInt(), bound.height().toInt(), Bitmap.Config.ARGB_8888)
        maskCanvas = Canvas(maskBitmap)

        if (bound.width() < bound.height()) {
            width = bound.width() / 3f
            height = width
        } else {
            width = bound.height() / 3f
            height = width
        }
    }

    fun contains(ex: Float, ey: Float): Boolean {
        return ex >= x && ex <= x + width && ey >= y && ey <= y + height
    }

    abstract fun processTouchEvent(event: MotionEvent): Boolean

    abstract fun draw(canvas: Canvas)

    fun move(dx: Float, dy: Float): Boolean {
        var deltaX = dx
        var deltaY = dy

        val top = y - dy
        if (top < 0) {
            deltaY = dy + top
        }

        val bottom = y + height - dy
        if (bottom > bound.height()) {
            deltaY = dy - bound.height() + bottom
        }

        val left = x - dx
        if (left < 0) {
            deltaX = dx + left
        }

        val right = x + width - dx
        if (right > bound.width()) {
            deltaX = dx - bound.width() + right
        }

        x -= deltaX
        y -= deltaY

        return true
    }

    override fun toString(): String {
        return "view x: $x, y: $y, width: $width, height: $height"
    }

    class CropBoxBuilder {
        var boxType: Int = DEFAULT_BOX_TYPE
        var minSize : Float = 0f
        var touchSlop: Float = 0f
        var bound: RectF = RectF()
        var boxColor: Int = 0
        var lineWidth: Int = 0
        var anchorSize: Int = 0

        fun createCropBox(): CropBox {
            return when (boxType) {
                RECT_CROP_BOX -> RectCropBox(minSize, touchSlop, bound, boxColor, lineWidth, anchorSize)
                else -> CircleCropBox(minSize, touchSlop, bound, boxColor, lineWidth, anchorSize)
            }
        }
    }
}
