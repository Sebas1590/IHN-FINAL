package com.example.practica_desarrollomovil.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.practica_desarrollomovil.domain.repository.SessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginViewModel(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    val isGuestSessionActive = sessionRepository.isGuestSessionActive
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun continueWithoutSession(onSuccess: () -> Unit) {
        viewModelScope.launch {
            sessionRepository.activateGuestSession()
            onSuccess()
        }
    }

    class Factory(
        private val sessionRepository: SessionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoginViewModel(sessionRepository) as T
        }
    }
}
