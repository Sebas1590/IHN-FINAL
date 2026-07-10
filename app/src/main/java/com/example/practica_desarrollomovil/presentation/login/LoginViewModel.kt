package com.example.practica_desarrollomovil.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.practica_desarrollomovil.domain.repository.SessionRepository
import com.example.practica_desarrollomovil.domain.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

class LoginViewModel(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val isGuestSessionActive = sessionRepository.isGuestSessionActive
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    /**
     * Intenta iniciar sesión. Si tiene éxito activa la sesión (la navegación reacciona sola);
     * en caso contrario informa el error concreto mediante [onError].
     */
    fun login(email: String, password: String, onError: (String) -> Unit) {
        val trimmedEmail = email.trim()
        when {
            trimmedEmail.isBlank() -> { onError("Ingresa tu correo"); return }
            !EMAIL_REGEX.matches(trimmedEmail) -> { onError("Ingresa un correo válido"); return }
            password.isBlank() -> { onError("Ingresa tu contraseña"); return }
        }

        viewModelScope.launch {
            val user = userRepository.authenticate(trimmedEmail, password)
            when {
                user != null -> sessionRepository.activateGuestSession()
                !userRepository.emailExists(trimmedEmail) ->
                    onError("No existe una cuenta con este correo. Regístrate primero.")
                else -> onError("Contraseña incorrecta")
            }
        }
    }

    fun continueWithoutSession(onSuccess: () -> Unit) {
        viewModelScope.launch {
            sessionRepository.activateGuestSession()
            onSuccess()
        }
    }

    class Factory(
        private val sessionRepository: SessionRepository,
        private val userRepository: UserRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoginViewModel(sessionRepository, userRepository) as T
        }
    }
}
