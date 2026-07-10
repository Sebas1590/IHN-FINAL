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
import com.example.practica_desarrollomovil.presentation.components.MetamercaCard
import com.example.practica_desarrollomovil.presentation.components.MetamercaSuccessDialog
import com.example.practica_desarrollomovil.presentation.components.PrimaryBrownButton
import com.example.practica_desarrollomovil.presentation.theme.BrandBrown
import com.example.practica_desarrollomovil.presentation.theme.TextSecondary

/**
 * Pantalla de registro puramente visual (demostración).
 * No guarda datos ni valida credenciales: sirve para mostrar cómo lucirá el flujo.
 */
@Composable
fun RegisterScreen(
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var showDemoDialog by remember { mutableStateOf(false) }

    BackHandler(onBack = onBack)

    if (showDemoDialog) {
        MetamercaSuccessDialog(
            onDismissRequest = { showDemoDialog = false; onBack() },
            onConfirm = { showDemoDialog = false; onBack() },
            onSecondaryAction = { showDemoDialog = false },
            title = "¡Cuenta creada!",
            text = "Esta es una vista de demostración. Así se verá el registro cuando el programa esté en funcionamiento.",
            confirmText = "Ir a iniciar sesión",
            secondaryText = "Seguir viendo"
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
                        value = name,
                        onValueChange = { name = it },
                        placeholder = "Jessica Pérez",
                        keyboardType = KeyboardType.Text
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LabeledField(
                        label = "Correo electrónico",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "ejemplo@ganancias.com",
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    PasswordField(
                        label = "Contraseña",
                        value = password,
                        onValueChange = { password = it },
                        visible = passwordVisible,
                        onToggleVisibility = { passwordVisible = !passwordVisible }
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    PasswordField(
                        label = "Confirmar contraseña",
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        visible = confirmVisible,
                        onToggleVisibility = { confirmVisible = !confirmVisible }
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    PrimaryBrownButton(
                        text = "Crear cuenta",
                        onClick = { showDemoDialog = true }
                    )
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
    keyboardType: KeyboardType
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
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onToggleVisibility: () -> Unit
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
}
