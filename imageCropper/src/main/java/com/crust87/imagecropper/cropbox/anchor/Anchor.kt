/*
 * ImageCropper
 * https://github.com/crust87/Android-ImageCropper
 *
 * Mabi
 * crust87@gmail.com
 * last modify 2019-01-18
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

package com.crust87.imagecropper.cropbox.anchor

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Anchor(var id: Int, var radius: Float, var x: Float = 0f, var y: Float = 0f) {

    var paint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        strokeWidth = 2f
    }

    private val touchSlop: Float = radius * 2

    constructor(id: Int, mRadius: Int) : this(id, mRadius.toFloat(), 0f, 0f)

    fun draw(canvas: Canvas) {
        canvas.drawCircle(x, y, radius, paint)
    }

    fun setLocation(newX: Float, newY: Float) {
        x = newX
        y = newY
    }

    fun contains(targetX: Float, targetY: Float): Boolean {
        return targetX >= x - touchSlop && targetX <= x + touchSlop && targetY >= y - touchSlop && targetY <= y + touchSlop
    }

    fun setColor(colorCode: String) {
        paint.color = Color.parseColor(colorCode)
    }

    fun setColor(color: Int) {
        paint.color = color
    }
}
