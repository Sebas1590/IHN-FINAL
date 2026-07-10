package com.example.practica_desarrollomovil.presentation.accessibility

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.text.style.TextAlign
import com.example.practica_desarrollomovil.data.local.preferences.AccessibilityPreferences
import com.example.practica_desarrollomovil.domain.model.AccessibilitySettings
import com.example.practica_desarrollomovil.domain.model.AccessibilityTool
import com.example.practica_desarrollomovil.presentation.components.MetamercaCard
import com.example.practica_desarrollomovil.presentation.components.PrimaryBrownButton
import com.example.practica_desarrollomovil.presentation.theme.BrandBrown
import com.example.practica_desarrollomovil.presentation.theme.CreamBackground
import com.example.practica_desarrollomovil.presentation.theme.TextSecondary

@Composable
fun AccessibilityScreen(
    viewModel: AccessibilityViewModel,
    onLogout: () -> Unit
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Cabecera: Perfil
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = BrandBrown,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = "Mi perfil",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                fontWeight = FontWeight.Bold,
                color = BrandBrown
            )
        }

        Text(
            text = "Gestiona tu cuenta y personaliza la experiencia de uso de la aplicación.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Tarjeta de Sesión
        MetamercaCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Sesión activa",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrandBrown
                )
                Text(
                    text = "Puedes cerrar sesión para volver a la pantalla de inicio.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
                PrimaryBrownButton(
                    text = "Cerrar sesión",
                    onClick = { viewModel.logout(onLogout) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Cabecera: Accesibilidad
        Text(
            text = "Accesibilidad",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = BrandBrown
        )
        Text(
            text = "Herramientas para mejorar la lectura y la interacción. Compatible con TalkBack.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Herramientas de Accesibilidad
        AccessibilityTool.entries.forEach { tool ->
            AccessibilityToolCard(
                tool = tool,
                level = settings.levelFor(tool),
                icon = iconFor(tool),
                onClick = { viewModel.cycleLevel(tool) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = CreamBackground
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { viewModel.resetAll() }
                .semantics {
                    contentDescription = "Restablecer todas las opciones de accesibilidad"
                }
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = null,
                tint = BrandBrown
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Restablecer",
                style = MaterialTheme.typography.titleMedium,
                color = BrandBrown,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun AccessibilityToolCard(
    tool: AccessibilityTool,
    level: Int,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val levelDescription = "Nivel $level de ${AccessibilityPreferences.MAX_LEVEL}. Toca para cambiar."

    MetamercaCard(
        modifier = modifier.clickable(
            onClickLabel = "Cambiar nivel de ${tool.title}",
            onClick = onClick
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CreamBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = BrandBrown,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tool.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandBrown
                    )
                    Text(
                        text = tool.talkBackDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LevelIndicatorBar(level = level)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Toca para aumentar el nivel (Ciclo: 0-${AccessibilityPreferences.MAX_LEVEL})",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.End),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun LevelIndicatorBar(level: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(AccessibilityPreferences.MAX_LEVEL + 1) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (index <= level) BrandBrown else CreamBackground
                    )
            )
        }
    }
}

private fun AccessibilitySettings.levelFor(tool: AccessibilityTool): Int = when (tool) {
    AccessibilityTool.TEXT_SIZE -> textSizeLevel
    AccessibilityTool.CONTRAST -> contrastLevel
    AccessibilityTool.READING_MASK -> readingMaskLevel
    AccessibilityTool.DYSLEXIA -> dyslexiaFriendlyLevel
}

private fun iconFor(tool: AccessibilityTool): ImageVector = when (tool) {
    AccessibilityTool.TEXT_SIZE -> Icons.Default.TextFields
    AccessibilityTool.CONTRAST -> Icons.Outlined.Contrast
    AccessibilityTool.READING_MASK -> Icons.Default.Visibility
    AccessibilityTool.DYSLEXIA -> Icons.Default.TextFields
}
