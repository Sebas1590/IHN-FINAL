package com.example.practica_desarrollomovil.presentation.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.withSaveLayer
import androidx.compose.ui.geometry.toRect
import com.example.practica_desarrollomovil.domain.model.AccessibilitySettings

val LocalAccessibilitySettings = staticCompositionLocalOf { AccessibilitySettings.Default }

val LocalMinTouchTarget = staticCompositionLocalOf { 48 }

@Composable
fun ProvideAccessibilitySettings(
    settings: AccessibilitySettings,
    content: @Composable () -> Unit
) {
    val contrastModifier = when (settings.contrastLevel) {
        1 -> Modifier.grayscale()
        2 -> Modifier.invert()
        3 -> Modifier.grayscale().invert()
        else -> Modifier
    }

    CompositionLocalProvider(
        LocalAccessibilitySettings provides settings,
        LocalMinTouchTarget provides settings.minTouchTargetDp
    ) {
        Box(modifier = contrastModifier) {
            content()
        }
    }
}

private fun Modifier.grayscale(): Modifier = this.drawWithCache {
    val matrix = ColorMatrix().apply { setToSaturation(0f) }
    val filter = ColorFilter.colorMatrix(matrix)
    val paint = Paint().apply { colorFilter = filter }
    onDrawWithContent {
        drawIntoCanvas { canvas ->
            canvas.withSaveLayer(size.toRect(), paint) {
                drawContent()
            }
        }
    }
}

private fun Modifier.invert(): Modifier = this.drawWithCache {
    val matrix = ColorMatrix(
        floatArrayOf(
            -1f, 0f, 0f, 0f, 255f,
            0f, -1f, 0f, 0f, 255f,
            0f, 0f, -1f, 0f, 255f,
            0f, 0f, 0f, 1f, 0f
        )
    )
    val filter = ColorFilter.colorMatrix(matrix)
    val paint = Paint().apply { colorFilter = filter }
    onDrawWithContent {
        drawIntoCanvas { canvas ->
            canvas.withSaveLayer(size.toRect(), paint) {
                drawContent()
            }
        }
    }
}
