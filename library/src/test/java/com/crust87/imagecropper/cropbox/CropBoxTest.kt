package com.crust87.imagecropper.cropbox

import android.graphics.RectF
import junit.framework.Assert.*
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
    fun givenDragLeft50_whenMove_thenMove50() {
        val cropBox = cropBoxBuilder.createCropBox()

        cropBox.move(50f, 0f)

        assertEquals(cropBox.x, 50f)
    }

    @Test
    fun givenDragLeft150_whenMove_thenMove100() {
        val cropBox = cropBoxBuilder.createCropBox()

        cropBox.move(150f, 0f)

        assertEquals(cropBox.x, 0f)
    }

    @Test
    fun givenDragTop50_whenMove_thenMove50() {
        val cropBox = cropBoxBuilder.createCropBox()

        cropBox.move(0f, 50f)

        assertEquals(cropBox.y, 50f)
    }

    @Test
    fun givenDragTop150_whenMove_thenMove100() {
        val cropBox = cropBoxBuilder.createCropBox()

        cropBox.move(0f, 150f)

        assertEquals(cropBox.y, 0f)
    }

    @Test
    fun givenDragRight50_whenMove_thenMove50() {
        val cropBox = cropBoxBuilder.createCropBox()

        cropBox.move(-50f, 0f)

        assertEquals(cropBox.x, 150f)
    }

    @Test
    fun givenDragRight150_whenMove_thenMove100() {
        val cropBox = cropBoxBuilder.createCropBox()

        cropBox.move(-150f, 0f)

        assertEquals(cropBox.x, 200f)
    }

    @Test
    fun givenDragBottom50_whenMove_thenMove50() {
        val cropBox = cropBoxBuilder.createCropBox()

        cropBox.move(0f, -50f)

        assertEquals(cropBox.y, 150f)
    }

    @Test
    fun givenDragBottom150_whenMove_thenMove100() {
        val cropBox = cropBoxBuilder.createCropBox()

        cropBox.move(0f, -150f)

        assertEquals(cropBox.y, 200f)
    }
}