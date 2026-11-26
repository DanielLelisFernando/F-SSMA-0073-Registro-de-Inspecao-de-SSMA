package br.com.ssma.inspecoes

import androidx.compose.ui.geometry.Offset

internal fun signatureHasDrawableStroke(strokes: List<List<Offset>>): Boolean =
    strokes.any { points ->
        points.size >= 2 && points.zipWithNext().any { (start, end) ->
            val dx = end.x - start.x
            val dy = end.y - start.y
            dx * dx + dy * dy > 1f
        }
    }
