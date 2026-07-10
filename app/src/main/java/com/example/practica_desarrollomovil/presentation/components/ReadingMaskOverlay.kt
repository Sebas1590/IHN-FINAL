package com.example.practica_desarrollomovil.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.practica_desarrollomovil.domain.model.AccessibilitySettings

@Composable
fun ReadingMaskOverlay(settings: AccessibilitySettings) {
    if (!settings.readingMaskEnabled) return

    val dimAlpha = when (settings.readingMaskLevel) {
        1 -> 0.45f
        2 -> 0.62f
        else -> 0.78f
    }

    val bandHeightFraction = when (settings.readingMaskLevel) {
        1 -> 0.34f
        2 -> 0.28f
        else -> 0.22f
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val bandHeight = size.height * bandHeightFraction
        val topHeight = (size.height - bandHeight) / 2f
        val bottomTop = topHeight + bandHeight
        val dimColor = Color.Black.copy(alpha = dimAlpha)

        drawRect(color = dimColor, topLeft = androidx.compose.ui.geometry.Offset.Zero, size = androidx.compose.ui.geometry.Size(size.width, topHeight))
        drawRect(
            color = dimColor,
            topLeft = androidx.compose.ui.geometry.Offset(0f, bottomTop),
            size = androidx.compose.ui.geometry.Size(size.width, size.height - bottomTop)
        )
    }
}
