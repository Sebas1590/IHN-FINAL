package com.example.practica_desarrollomovil.presentation.login

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.practica_desarrollomovil.presentation.components.MetamercaCard
import com.example.practica_desarrollomovil.presentation.components.MetamercaSuccessDialog
import com.example.practica_desarrollomovil.presentation.components.PrimaryBrownButton
import com.example.practica_desarrollomovil.presentation.theme.BrandBrown
import com.example.practica_desarrollomovil.presentation.theme.CancelRed
import com.example.practica_desarrollomovil.presentation.theme.TextSecondary

/**
 * Pantalla de registro con validación real por campo.
 * Al crear la cuenta correctamente, el usuario queda guardado y puede iniciar sesión.
 */
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    BackHandler(onBack = onBack)

    if (uiState.registeredSuccessfully) {
        MetamercaSuccessDialog(
            onDismissRequest = { viewModel.consumeRegistered(); onBack() },
            onConfirm = { viewModel.consumeRegistered(); onBack() },
            onSecondaryAction = { viewModel.consumeRegistered(); onBack() },
            title = "¡Cuenta creada!",
            text = "Tu cuenta se registró correctamente. Ya puedes iniciar sesión con tu correo y contraseña.",
            confirmText = "Ir a iniciar sesión",
            secondaryText = "Cerrar"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp, start = 8.dp)) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver a iniciar sesión",
                    tint = BrandBrown
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crear cuenta",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = BrandBrown
            )
            Text(
                text = "Regístrate para empezar a gestionar tu negocio",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            MetamercaCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    LabeledField(
                        label = "Nombre completo",
                        value = uiState.name,
                        onValueChange = viewModel::onNameChange,
                        placeholder = "Nombre y apellido",
                        keyboardType = KeyboardType.Text,
                        error = uiState.nameError
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LabeledField(
                        label = "Correo electrónico",
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        placeholder = "correo@ejemplo.com",
                        keyboardType = KeyboardType.Email,
                        error = uiState.emailError
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    PasswordField(
                        label = "Contraseña",
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChange,
                        visible = passwordVisible,
                        onToggleVisibility = { passwordVisible = !passwordVisible },
                        error = uiState.passwordError
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    PasswordField(
                        label = "Confirmar contraseña",
                        value = uiState.confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        visible = confirmVisible,
                        onToggleVisibility = { confirmVisible = !confirmVisible },
                        error = uiState.confirmError
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        PrimaryBrownButton(
                            text = "Crear cuenta",
                            onClick = viewModel::register
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    error: String?
) {
    Text(text = label, style = MaterialTheme.typography.labelLarge, color = BrandBrown)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        placeholder = { Text(placeholder) },
        singleLine = true,
        isError = error != null,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
    FieldError(error)
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onToggleVisibility: () -> Unit,
    error: String?
) {
    Text(text = label, style = MaterialTheme.typography.labelLarge, color = BrandBrown)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        placeholder = { Text("••••••••") },
        singleLine = true,
        isError = error != null,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña",
                    tint = BrandBrown
                )
            }
        }
    )
    FieldError(error)
}

@Composable
private fun FieldError(error: String?) {
    if (error != null) {
        Text(
            text = error,
            color = CancelRed,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )
    }
}
