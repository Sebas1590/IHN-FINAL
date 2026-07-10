package com.example.practica_desarrollomovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.practica_desarrollomovil.domain.model.AccessibilitySettings
import com.example.practica_desarrollomovil.presentation.components.ReadingMaskOverlay
import com.example.practica_desarrollomovil.presentation.navigation.MetamercaNavHost
import com.example.practica_desarrollomovil.presentation.theme.MetamercaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as MetamercaApp).container
        setContent {
            val accessibilitySettings = container.accessibilityPreferences.settings
                .collectAsStateWithLifecycle(initialValue = AccessibilitySettings.Default)
                .value

            MetamercaTheme(accessibilitySettings = accessibilitySettings) {
                Box(modifier = Modifier.fillMaxSize()) {
                    MetamercaNavHost(container = container)
                    ReadingMaskOverlay(settings = accessibilitySettings)
                }
            }
        }
    }
}
