package com.example.practica_desarrollomovil.presentation.accessibility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.practica_desarrollomovil.data.local.preferences.AccessibilityPreferences
import com.example.practica_desarrollomovil.domain.model.AccessibilitySettings
import com.example.practica_desarrollomovil.domain.model.AccessibilityTool
import com.example.practica_desarrollomovil.domain.repository.SessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccessibilityViewModel(
    private val accessibilityPreferences: AccessibilityPreferences,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    val settings: StateFlow<AccessibilitySettings> = accessibilityPreferences.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AccessibilitySettings.Default)

    fun increaseLevel(tool: AccessibilityTool) {
        val current = settings.value.levelFor(tool)
        if (current >= AccessibilityPreferences.MAX_LEVEL) return
        viewModelScope.launch {
            accessibilityPreferences.updateLevel(tool.storageKey(), current + 1)
        }
    }

    fun decreaseLevel(tool: AccessibilityTool) {
        val current = settings.value.levelFor(tool)
        if (current <= 0) return
        viewModelScope.launch {
            accessibilityPreferences.updateLevel(tool.storageKey(), current - 1)
        }
    }

    fun resetAll() {
        viewModelScope.launch {
            accessibilityPreferences.resetAll()
        }
    }

    fun cycleLevel(tool: AccessibilityTool) {
        val current = settings.value.levelFor(tool)
        val nextLevel = if (current >= AccessibilityPreferences.MAX_LEVEL) 0 else current + 1
        viewModelScope.launch {
            accessibilityPreferences.updateLevel(tool.storageKey(), nextLevel)
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            sessionRepository.clearGuestSession()
            onLoggedOut()
        }
    }

    class Factory(
        private val accessibilityPreferences: AccessibilityPreferences,
        private val sessionRepository: SessionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AccessibilityViewModel(accessibilityPreferences, sessionRepository) as T
        }
    }
}

private fun AccessibilitySettings.levelFor(tool: AccessibilityTool): Int = when (tool) {
    AccessibilityTool.TEXT_SIZE -> textSizeLevel
    AccessibilityTool.CONTRAST -> contrastLevel
    AccessibilityTool.READING_MASK -> readingMaskLevel
    AccessibilityTool.DYSLEXIA -> dyslexiaFriendlyLevel
}

private fun AccessibilityTool.storageKey(): String = when (this) {
    AccessibilityTool.TEXT_SIZE -> AccessibilityPreferences.TEXT_SIZE_KEY.name
    AccessibilityTool.CONTRAST -> AccessibilityPreferences.CONTRAST_KEY.name
    AccessibilityTool.READING_MASK -> AccessibilityPreferences.READING_MASK_KEY.name
    AccessibilityTool.DYSLEXIA -> AccessibilityPreferences.DYSLEXIA_KEY.name
}
