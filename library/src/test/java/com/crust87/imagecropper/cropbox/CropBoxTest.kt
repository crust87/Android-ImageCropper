package com.crust87.imagecropper.cropbox

import android.graphics.RectF
import org.junit.Test

class CropBoxTest {

    val cropBoxBuilder = CropBox.CropBoxBuilder().apply {
        minSize = 10f
        leftMargin = 0f
        topMargin = 0f
        bound = RectF().apply {
            left = 0f
            top = 0f
            right = 100f
            bottom = 100f
        }


    }

    @Test
    fun given_when_then() {
        val cropBox = cropBoxBuilder.createCropBox()

        cropBox.contains(50f, 50f)
    }
}