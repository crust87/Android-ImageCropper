package com.crust87.imagecropper.cropbox

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnchorTest {

    @Test
    fun givenTouchSlop10_whenContains_thenTrue() {
        val anchor = Anchor(0, 40f, 10).apply {
            x = 50f
            y = 50f
        }

        val contains = anchor.contains(99f, 99f)

        assertTrue(contains)
    }

    @Test
    fun givenTouchSlop0_whenContains_thenFalse() {
        val anchor = Anchor(0, 40f, 0).apply {
            x = 50f
            y = 50f
        }

        val contains = anchor.contains(91f, 91f)

        assertFalse(contains)
    }
}