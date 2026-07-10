package com.example.practica_desarrollomovil.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.practica_desarrollomovil.domain.model.AccessibilitySettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.accessibilityDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "metamerca_accessibility"
)

class AccessibilityPreferences(private val context: Context) {

    val settings: Flow<AccessibilitySettings> = context.accessibilityDataStore.data.map { prefs ->
        AccessibilitySettings(
            textSizeLevel = prefs[TEXT_SIZE_KEY] ?: 0,
            contrastLevel = prefs[CONTRAST_KEY] ?: 0,
            readingMaskLevel = prefs[READING_MASK_KEY] ?: 0,
            dyslexiaFriendlyLevel = prefs[DYSLEXIA_KEY] ?: 0
        )
    }

    suspend fun updateLevel(toolKey: String, level: Int) {
        val clamped = level.coerceIn(0, MAX_LEVEL)
        context.accessibilityDataStore.edit { prefs ->
            when (toolKey) {
                TEXT_SIZE_KEY.name -> prefs[TEXT_SIZE_KEY] = clamped
                CONTRAST_KEY.name -> prefs[CONTRAST_KEY] = clamped
                READING_MASK_KEY.name -> prefs[READING_MASK_KEY] = clamped
                DYSLEXIA_KEY.name -> prefs[DYSLEXIA_KEY] = clamped
            }
        }
    }

    suspend fun resetAll() {
        context.accessibilityDataStore.edit { it.clear() }
    }

    companion object {
        const val MAX_LEVEL = 3

        val TEXT_SIZE_KEY = intPreferencesKey("text_size_level")
        val CONTRAST_KEY = intPreferencesKey("contrast_level")
        val READING_MASK_KEY = intPreferencesKey("reading_mask_level")
        val DYSLEXIA_KEY = intPreferencesKey("dyslexia_level")
    }
}
