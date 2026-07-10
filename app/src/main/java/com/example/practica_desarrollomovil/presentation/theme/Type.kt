package com.example.practica_desarrollomovil.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.practica_desarrollomovil.domain.model.AccessibilitySettings

private fun scaledStyle(
    settings: AccessibilitySettings,
    fontSize: Float,
    fontWeight: FontWeight,
    color: androidx.compose.ui.graphics.Color
): TextStyle {
    val scaledSize = (fontSize * settings.textScaleFactor).sp
    val lineHeight = (scaledSize.value * settings.lineHeightMultiplier).sp
    return TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = fontWeight,
        fontSize = scaledSize,
        lineHeight = lineHeight,
        letterSpacing = settings.letterSpacingEm.sp,
        color = color
    )
}

fun metamercaTypography(settings: AccessibilitySettings): Typography {
    val importantColor = if (settings.highContrast) BrandBrownDark else BrandBrown
    val bodyColor = if (settings.highContrast) TextPrimary else TextPrimary
    val secondaryColor = if (settings.highContrast) TextPrimary else TextSecondary

    return Typography(
        headlineMedium = scaledStyle(settings, 22f, FontWeight.Bold, importantColor),
        titleLarge = scaledStyle(settings, 20f, FontWeight.Bold, importantColor),
        titleMedium = scaledStyle(settings, 16f, FontWeight.SemiBold, importantColor),
        bodyLarge = scaledStyle(settings, 16f, FontWeight.Normal, bodyColor),
        bodyMedium = scaledStyle(settings, 14f, FontWeight.Normal, secondaryColor),
        labelLarge = scaledStyle(settings, 14f, FontWeight.SemiBold, importantColor)
    )
}
