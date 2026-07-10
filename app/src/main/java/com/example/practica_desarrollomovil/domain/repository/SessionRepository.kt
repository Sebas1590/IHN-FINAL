package com.example.practica_desarrollomovil.domain.repository

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    val isGuestSessionActive: Flow<Boolean>
    suspend fun activateGuestSession()
    suspend fun clearGuestSession()
}
