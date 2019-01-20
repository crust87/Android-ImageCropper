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

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import com.crust87.imagecropper.R

abstract class CropBox(context: Context, val leftMargin: Float, val topMargin: Float, val bound: Rect, var boxColor: Int, val lineWidth: Int) {

    enum class Action {
        Resize, Move, None
    }

    val minSize = context.resources.getDimensionPixelSize(R.dimen.min_box_Size).toFloat()

    val paint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = lineWidth.toFloat()
        color = boxColor
        strokeWidth = lineWidth.toFloat()
    }

    val backgroundPaint = Paint().apply {
        color = Color.RED
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
        maskBitmap = Bitmap.createBitmap(bound.width(), bound.height(), Bitmap.Config.ARGB_8888)
        maskCanvas = Canvas(maskBitmap)
    }

    fun contains(ex: Float, ey: Float): Boolean {
        return ex >= x && ex <= x + width && ey >= y && ey <= y + height
    }

    abstract fun processTouchEvent(event: MotionEvent): Boolean

    abstract fun draw(canvas: Canvas)

    override fun toString(): String {
        return "view x: $x, y: $y, width: $width, height: $height"
    }

    class CropBoxBuilder {
        private var newBoxType: Int = 0
        private var newLeftMargin: Float = 0.toFloat()
        private var newTopMargin: Float = 0.toFloat()
        private var newBound: Rect = Rect()
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
            return CircleCropBox(context, newLeftMargin, newTopMargin, newBound, newBoxColor, newLineWidth, newAnchorSize)
        }
    }
}
