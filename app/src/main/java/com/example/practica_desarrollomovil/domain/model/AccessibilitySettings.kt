package com.example.practica_desarrollomovil.domain.model

data class AccessibilitySettings(
    val textSizeLevel: Int = 0,
    val contrastLevel: Int = 0,
    val readingMaskLevel: Int = 0,
    val dyslexiaFriendlyLevel: Int = 0
) {
    val textScaleFactor: Float
        get() = when (textSizeLevel) {
            1 -> 1.12f
            2 -> 1.24f
            3 -> 1.36f
            else -> 1f
        }

    val letterSpacingEm: Float
        get() = when (dyslexiaFriendlyLevel) {
            1 -> 0.04f
            2 -> 0.08f
            3 -> 0.12f
            else -> 0f
        }

    val lineHeightMultiplier: Float
        get() = when (dyslexiaFriendlyLevel) {
            1 -> 1.35f
            2 -> 1.5f
            3 -> 1.65f
            else -> 1.2f
        }

    val readingMaskEnabled: Boolean
        get() = readingMaskLevel > 0

    val highContrast: Boolean
        get() = contrastLevel > 0

    val isGrayscale: Boolean
        get() = contrastLevel == 1 || contrastLevel == 3

    val isInverted: Boolean
        get() = contrastLevel == 2 || contrastLevel == 3

    val minTouchTargetDp: Int
        get() = when (textSizeLevel) {
            1 -> 56
            2 -> 64
            3 -> 72
            else -> 48
        }

    companion object {
        val Default = AccessibilitySettings()
    }
}

enum class AccessibilityTool(
    val title: String,
    val talkBackDescription: String
) {
    TEXT_SIZE("Tamaño de texto", "Ajusta el tamaño del texto en toda la aplicación"),
    CONTRAST("Contrastes", "Aumenta el contraste entre texto y fondo para mejor lectura"),
    READING_MASK("Máscara de lectura", "Enfoca la lectura resaltando una franja central"),
    DYSLEXIA("Dislexia amigable", "Aumenta el espaciado entre letras para facilitar la lectura")
}
