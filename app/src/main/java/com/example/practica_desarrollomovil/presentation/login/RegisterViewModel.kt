package com.example.practica_desarrollomovil.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.practica_desarrollomovil.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private const val MIN_PASSWORD_LENGTH = 6

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null,
    val isLoading: Boolean = false,
    val registeredSuccessfully: Boolean = false
)

class RegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, nameError = null) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, emailError = null) }
    fun onPasswordChange(value: String) = _uiState.update {
        it.copy(password = value, passwordError = null, confirmError = null)
    }
    fun onConfirmPasswordChange(value: String) = _uiState.update {
        it.copy(confirmPassword = value, confirmError = null)
    }

    fun register() {
        val state = _uiState.value

        val nameError = if (state.name.isBlank()) "Ingresa tu nombre" else null
        val emailError = when {
            state.email.isBlank() -> "Ingresa tu correo"
            !EMAIL_REGEX.matches(state.email.trim()) -> "Ingresa un correo válido (ej. correo@ejemplo.com)"
            else -> null
        }
        val passwordError = when {
            state.password.isBlank() -> "Crea una contraseña"
            state.password.length < MIN_PASSWORD_LENGTH ->
                "La contraseña debe tener al menos $MIN_PASSWORD_LENGTH caracteres"
            else -> null
        }
        val confirmError = when {
            state.confirmPassword.isBlank() -> "Repite la contraseña"
            state.confirmPassword != state.password -> "Las contraseñas no coinciden"
            else -> null
        }

        if (nameError != null || emailError != null || passwordError != null || confirmError != null) {
            _uiState.update {
                it.copy(
                    nameError = nameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmError = confirmError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                if (userRepository.emailExists(state.email)) {
                    _uiState.update {
                        it.copy(isLoading = false, emailError = "Ya existe una cuenta con este correo")
                    }
                    return@launch
                }
                userRepository.register(
                    name = state.name,
                    email = state.email,
                    password = state.password
                )
                _uiState.update { it.copy(isLoading = false, registeredSuccessfully = true) }
            } catch (e: Exception) {
                // Respaldo por si el correo se duplicó justo antes de insertar.
                _uiState.update {
                    it.copy(isLoading = false, emailError = "No se pudo crear la cuenta. Intenta con otro correo.")
                }
            }
        }
    }

    fun consumeRegistered() = _uiState.update { it.copy(registeredSuccessfully = false) }

    class Factory(
        private val userRepository: UserRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RegisterViewModel(userRepository) as T
        }
    }
}
