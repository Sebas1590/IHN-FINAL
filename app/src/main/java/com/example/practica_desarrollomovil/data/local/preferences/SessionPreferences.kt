package com.example.practica_desarrollomovil.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "metamerca_session"
)

class SessionPreferences(private val context: Context) {
    private val guestSessionKey = booleanPreferencesKey("guest_session_active")

    val isGuestSessionActive: Flow<Boolean> = context.sessionDataStore.data.map { prefs ->
        prefs[guestSessionKey] == true
    }

    suspend fun setGuestSessionActive(active: Boolean) {
        context.sessionDataStore.edit { prefs ->
            prefs[guestSessionKey] = active
        }
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { it.clear() }
    }
}
