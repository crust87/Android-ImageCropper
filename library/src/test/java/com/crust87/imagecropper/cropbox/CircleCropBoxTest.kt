package com.crust87.imagecropper.cropbox

import android.graphics.RectF
import com.crust87.imagecropper.ImageCropper
import junit.framework.TestCase
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CircleCropBoxTest {

    val cropBoxBuilder = CropBox.CropBoxBuilder().apply {
        boxType = ImageCropper.CIRCLE_CROP_BOX
        minSize = 10f
        bound = RectF().apply {
            left = 0f
            top = 0f
            right = 300f
            bottom = 300f
        }
    }

    @Test
    fun givenCenter_whenResize_thenScaled100() {
        val cropBox = cropBoxBuilder.createCropBox() as CircleCropBox

        cropBox.scale(-50f)

        TestCase.assertEquals(50f, cropBox.x)
        TestCase.assertEquals(50f, cropBox.y)
        TestCase.assertEquals(200f, cropBox.width)
        TestCase.assertEquals(200f, cropBox.height)
    }

    @Test
    fun givenTopLeft_whenResize_thenScaled100() {
        val cropBox = cropBoxBuilder.createCropBox() as CircleCropBox
        cropBox.centerX = 50f
        cropBox.centerY = 50f

        cropBox.scale(-50f)

        TestCase.assertEquals(0f, cropBox.x)
        TestCase.assertEquals(0f, cropBox.y)
        TestCase.assertEquals(200f, cropBox.width)
        TestCase.assertEquals(200f, cropBox.height)
    }

    @Test
    fun givenTopRight_whenResize_thenScaled100() {
        val cropBox = cropBoxBuilder.createCropBox() as CircleCropBox
        cropBox.centerX = 250f
        cropBox.centerY = 50f

        cropBox.scale(-50f)

        TestCase.assertEquals(100f, cropBox.x)
        TestCase.assertEquals(0f, cropBox.y)
        TestCase.assertEquals(200f, cropBox.width)
        TestCase.assertEquals(200f, cropBox.height)
    }

    @Test
    fun givenBottomRight_whenResize_thenScaled100() {
        val cropBox = cropBoxBuilder.createCropBox() as CircleCropBox
        cropBox.centerX = 250f
        cropBox.centerY = 250f

        cropBox.scale(-50f)

        TestCase.assertEquals(100f, cropBox.x)
        TestCase.assertEquals(100f, cropBox.y)
        TestCase.assertEquals(200f, cropBox.width)
        TestCase.assertEquals(200f, cropBox.height)
    }

    @Test
    fun givenBottomLeft_whenResize_thenScaled100() {
        val cropBox = cropBoxBuilder.createCropBox() as CircleCropBox
        cropBox.centerX = 50f
        cropBox.centerY = 250f

        cropBox.scale(-50f)

        TestCase.assertEquals(0f, cropBox.x)
        TestCase.assertEquals(100f, cropBox.y)
        TestCase.assertEquals(200f, cropBox.width)
        TestCase.assertEquals(200f, cropBox.height)
    }
}
