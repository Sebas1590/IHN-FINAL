package com.example.practica_desarrollomovil.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.practica_desarrollomovil.domain.model.AccessibilitySettings

private fun colorSchemeFor(settings: AccessibilitySettings) = when (settings.contrastLevel) {
    3 -> lightColorScheme(
        primary = BrandBrownDark,
        onPrimary = Color.White,
        secondary = BrandBrown,
        onSecondary = Color.White,
        tertiary = BrandOrange,
        background = Color.White,
        onBackground = Color(0xFF1A120B),
        surface = Color.White,
        onSurface = Color(0xFF1A120B),
        surfaceVariant = Color(0xFFF0EBE3),
        onSurfaceVariant = Color(0xFF3D342B)
    )
    2 -> lightColorScheme(
        primary = BrandBrownDark,
        onPrimary = Color.White,
        secondary = BrandBrown,
        onSecondary = Color.White,
        tertiary = BrandOrange,
        background = Color(0xFFFFFBF5),
        onBackground = Color(0xFF2A2218),
        surface = Color.White,
        onSurface = Color(0xFF2A2218),
        surfaceVariant = Color(0xFFEDE6DC),
        onSurfaceVariant = Color(0xFF4A4035)
    )
    1 -> lightColorScheme(
        primary = BrandBrown,
        onPrimary = Color.White,
        secondary = BrandBrownDark,
        onSecondary = Color.White,
        tertiary = BrandOrange,
        background = CreamBackground,
        onBackground = TextPrimary,
        surface = CardWhite,
        onSurface = TextPrimary,
        surfaceVariant = Color(0xFFEDE6DC),
        onSurfaceVariant = TextSecondary
    )
    else -> lightColorScheme(
        primary = BrandBrown,
        onPrimary = Color.White,
        secondary = BrandTeal,
        onSecondary = Color.White,
        tertiary = BrandOrange,
        background = CreamBackground,
        onBackground = TextPrimary,
        surface = CardWhite,
        onSurface = TextPrimary,
        surfaceVariant = Color(0xFFEDE6DC),
        onSurfaceVariant = TextSecondary
    )
}

@Composable
fun MetamercaTheme(
    accessibilitySettings: AccessibilitySettings = AccessibilitySettings.Default,
    content: @Composable () -> Unit
) {
    ProvideAccessibilitySettings(accessibilitySettings) {
        MaterialTheme(
            colorScheme = colorSchemeFor(accessibilitySettings),
            typography = metamercaTypography(accessibilitySettings),
            content = content
        )
    }
}
