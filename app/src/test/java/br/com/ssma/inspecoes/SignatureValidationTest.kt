package br.com.ssma.inspecoes

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SignatureValidationTest {
    @Test fun rejectsEmptyAndSinglePointStrokes() {
        assertFalse(signatureHasDrawableStroke(emptyList()))
        assertFalse(signatureHasDrawableStroke(listOf(listOf(Offset(10f, 10f)))))
    }

    @Test fun rejectsStationaryStroke() {
        assertFalse(signatureHasDrawableStroke(listOf(listOf(Offset(10f, 10f), Offset(10f, 10f)))))
    }

    @Test fun acceptsVisibleMovement() {
        assertTrue(signatureHasDrawableStroke(listOf(listOf(Offset(10f, 10f), Offset(12f, 12f)))))
    }

    @Test fun ignoresInvalidStrokesButAcceptsAnyVisibleStroke() {
        val strokes = listOf(
            listOf(Offset(1f, 1f)),
            listOf(Offset(5f, 5f), Offset(5f, 5f)),
            listOf(Offset(10f, 10f), Offset(20f, 15f))
        )
        assertTrue(signatureHasDrawableStroke(strokes))
    }
}
