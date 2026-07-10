package com.example.practica_desarrollomovil.data.repository

import com.example.practica_desarrollomovil.data.local.preferences.SessionPreferences
import com.example.practica_desarrollomovil.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow

class SessionRepositoryImpl(
    private val sessionPreferences: SessionPreferences
) : SessionRepository {

    override val isGuestSessionActive: Flow<Boolean> =
        sessionPreferences.isGuestSessionActive

    override suspend fun activateGuestSession() {
        sessionPreferences.setGuestSessionActive(true)
    }

    override suspend fun clearGuestSession() {
        sessionPreferences.clearSession()
    }
}
