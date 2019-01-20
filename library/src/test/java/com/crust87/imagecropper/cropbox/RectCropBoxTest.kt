package com.crust87.imagecropper.cropbox

import android.graphics.RectF
import com.crust87.imagecropper.ImageCropper
import com.crust87.imagecropper.cropbox.RectCropBox.Companion.BOTTOM_LEFT
import com.crust87.imagecropper.cropbox.RectCropBox.Companion.BOTTOM_RIGHT
import com.crust87.imagecropper.cropbox.RectCropBox.Companion.TOP_LEFT
import com.crust87.imagecropper.cropbox.RectCropBox.Companion.TOP_RIGHT
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RectCropBoxTest {

    val cropBoxBuilder = CropBox.CropBoxBuilder().apply {
        boxType = ImageCropper.RECT_CROP_BOX
        minSize = 10f
        bound = RectF().apply {
            left = 0f
            top = 0f
            right = 300f
            bottom = 300f
        }
    }

    @Test
    fun givenDragTopLeft50_whenResize_thenResized50() {
        val cropBox = cropBoxBuilder.createCropBox() as RectCropBox
        cropBox.currentAnchor = TOP_LEFT

        cropBox.resize(50f, 50f)

        assertEquals(50f, cropBox.x)
        assertEquals(50f, cropBox.y)
        assertEquals(150f, cropBox.width)
        assertEquals(150f, cropBox.height)
    }

    @Test
    fun givenDragTopLeft150_whenResize_thenResized100() {
        val cropBox = cropBoxBuilder.createCropBox() as RectCropBox
        cropBox.currentAnchor = TOP_LEFT

        cropBox.resize(150f, 150f)

        assertEquals(0f, cropBox.x)
        assertEquals(0f, cropBox.y)
        assertEquals(200f, cropBox.width)
        assertEquals(200f, cropBox.height)
    }

    @Test
    fun givenDragTopRight50_whenResize_thenResized50() {
        val cropBox = cropBoxBuilder.createCropBox() as RectCropBox
        cropBox.currentAnchor = TOP_RIGHT

        cropBox.resize(-50f, 50f)

        assertEquals(100f, cropBox.x)
        assertEquals(50f, cropBox.y)
        assertEquals(150f, cropBox.width)
        assertEquals(150f, cropBox.height)
    }

    @Test
    fun givenDragTopRight150_whenResize_thenResized100() {
        val cropBox = cropBoxBuilder.createCropBox() as RectCropBox
        cropBox.currentAnchor = TOP_RIGHT

        cropBox.resize(-150f, 150f)

        assertEquals(100f, cropBox.x)
        assertEquals(0f, cropBox.y)
        assertEquals(200f, cropBox.width)
        assertEquals(200f, cropBox.height)
    }

    @Test
    fun givenDragBottomRight50_whenResize_thenResized50() {
        val cropBox = cropBoxBuilder.createCropBox() as RectCropBox
        cropBox.currentAnchor = BOTTOM_RIGHT

        cropBox.resize(-50f, -50f)

        assertEquals(100f, cropBox.x)
        assertEquals(100f, cropBox.y)
        assertEquals(150f, cropBox.width)
        assertEquals(150f, cropBox.height)
    }

    @Test
    fun givenDragTopBottomRight_whenResize_thenResized100() {
        val cropBox = cropBoxBuilder.createCropBox() as RectCropBox
        cropBox.currentAnchor = BOTTOM_RIGHT

        cropBox.resize(-150f, -150f)

        assertEquals(100f, cropBox.x)
        assertEquals(100f, cropBox.y)
        assertEquals(200f, cropBox.width)
        assertEquals(200f, cropBox.height)
    }

    @Test
    fun givenDragBottomLeft50_whenResize_thenResized50() {
        val cropBox = cropBoxBuilder.createCropBox() as RectCropBox
        cropBox.currentAnchor = BOTTOM_LEFT

        cropBox.resize(50f, -50f)

        assertEquals(50f, cropBox.x)
        assertEquals(100f, cropBox.y)
        assertEquals(150f, cropBox.width)
        assertEquals(150f, cropBox.height)
    }

    @Test
    fun givenDragBottomLeft150_whenResize_thenResized100() {
        val cropBox = cropBoxBuilder.createCropBox() as RectCropBox
        cropBox.currentAnchor = BOTTOM_LEFT

        cropBox.resize(150f, -150f)

        assertEquals(0f, cropBox.x)
        assertEquals(100f, cropBox.y)
        assertEquals(200f, cropBox.width)
        assertEquals(200f, cropBox.height)
    }
}