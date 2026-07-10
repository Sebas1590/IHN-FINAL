package com.example.practica_desarrollomovil.presentation.login

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.practica_desarrollomovil.presentation.components.MetamercaCard
import com.example.practica_desarrollomovil.presentation.components.MetamercaLogo
import com.example.practica_desarrollomovil.presentation.components.OutlinedBrownButton
import com.example.practica_desarrollomovil.presentation.components.PrimaryBrownButton
import com.example.practica_desarrollomovil.presentation.theme.AccentLink
import com.example.practica_desarrollomovil.presentation.theme.BrandBrown
import com.example.practica_desarrollomovil.presentation.theme.TextSecondary

@Composable
fun LoginScreen(
    onContinueWithoutSession: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        MetamercaLogo()
        Spacer(modifier = Modifier.height(24.dp))

        MetamercaCard {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Bienvenido de nuevo",
                    style = MaterialTheme.typography.titleLarge,
                    color = BrandBrown,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(text = "Correo electrónico", style = MaterialTheme.typography.labelLarge, color = BrandBrown)
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 12.dp),
                    placeholder = { Text("ejemplo@ganancias.com") },
                    singleLine = true,
                    enabled = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Text(text = "Contraseña", style = MaterialTheme.typography.labelLarge, color = BrandBrown)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 20.dp),
                    placeholder = { Text("••••••••") },
                    singleLine = true,
                    enabled = false,
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }, enabled = false) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    }
                )

                PrimaryBrownButton(
                    text = "Iniciar sesión →",
                    onClick = { },
                    enabled = false
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedBrownButton(
                    text = "Continuar sin sesión",
                    onClick = onContinueWithoutSession
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = buildAnnotatedString {
                append("¿No tienes una cuenta? ")
                withStyle(SpanStyle(color = AccentLink, fontWeight = FontWeight.Bold)) {
                    append("Regístrate")
                }
            },
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}
