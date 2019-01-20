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

package com.crust87.imagecropper.cropbox

import android.graphics.Canvas
import android.graphics.Paint

class Anchor(var id: Int, var radius: Float, touchSlop: Int) {

    var x: Float = 0f
    var y: Float = 0f
    var touchBound = radius + touchSlop

    fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawCircle(x, y, radius, paint)
    }

    fun setLocation(newX: Float, newY: Float) {
        x = newX
        y = newY
    }

    fun contains(targetX: Float, targetY: Float): Boolean {
        return targetX >= x - touchBound && targetX <= x + touchBound && targetY >= y - touchBound && targetY <= y + touchBound
    }
}
