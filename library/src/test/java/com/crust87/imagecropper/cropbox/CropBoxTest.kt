package com.crust87.imagecropper.cropbox

import android.graphics.RectF
import junit.framework.TestCase.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CropBoxTest {

    val cropBoxBuilder = CropBox.CropBoxBuilder().apply {
        minSize = 10f
        bound = RectF().apply {
            left = 0f
            top = 0f
            right = 300f
            bottom = 300f
        }
    }

    @Test
    fun givenCenterPoint_whenContains_thenTrue() {
        val cropBox = cropBoxBuilder.createCropBox()

        val contains = cropBox.contains(150f, 150f)

        assertTrue(contains)
    }

    @Test
    fun givenEdgePoint_whenContains_thenFalse() {
        val cropBox = cropBoxBuilder.createCropBox()

        val contains = cropBox.contains(201f, 201f)

        assertFalse(contains)
    }

    @Test
    fun givenDragLeft50_whenMove_thenMoveLeft50() {
        val cropBox = cropBoxBuilder.createCropBox()

        cropBox.move(50f, 0f)

        assertEquals(50f, cropBox.x)
    }

    @Test
    fun givenDragLeft150_whenMove_thenMoveLeft100() {
        val cropBox = cropBoxBuilder.createCropBox()

        cropBox.move(150f, 0f)

        assertEquals(0f, cropBox.x)
    }

    @Test
    fun givenDragTop50_whenMove_thenMoveTop50() {
        val cropBox = cropBoxBuilder.createCropBox()

        cropBox.move(0f, 50f)

        assertEquals(50f, cropBox.y)
    }

    @Test
    fun givenDragTop150_whenMove_thenMoveTop100() {
        val cropBox = cropBoxBuilder.createCropBox()

        cropBox.move(0f, 150f)

        assertEquals(0f, cropBox.y)
    }

    @Test
    fun givenDragRight50_whenMove_thenMoveRight50() {
        val cropBox = cropBoxBuilder.createCropBox()

        cropBox.move(-50f, 0f)

        assertEquals(150f, cropBox.x)
    }

    @Test
    fun givenDragRight150_whenMove_thenMoveRight100() {
        val cropBox = cropBoxBuilder.createCropBox()

        cropBox.move(-150f, 0f)

        assertEquals(200f, cropBox.x)
    }

    @Test
    fun givenDragBottom50_whenMove_thenMoveBottom50() {
        val cropBox = cropBoxBuilder.createCropBox()

        cropBox.move(0f, -50f)

        assertEquals(150f, cropBox.y)
    }

    @Test
    fun givenDragBottom150_whenMove_thenBottomMove100() {
        val cropBox = cropBoxBuilder.createCropBox()

        cropBox.move(0f, -150f)

        assertEquals(200f, cropBox.y)
    }
}