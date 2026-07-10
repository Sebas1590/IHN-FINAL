package com.example.practica_desarrollomovil.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.practica_desarrollomovil.domain.model.LowStockItem
import com.example.practica_desarrollomovil.presentation.theme.BrandOrange
import com.example.practica_desarrollomovil.presentation.theme.CancelRed
import com.example.practica_desarrollomovil.presentation.theme.CreamBackground
import com.example.practica_desarrollomovil.presentation.theme.TextPrimary
import com.example.practica_desarrollomovil.presentation.theme.TextSecondary
import com.example.practica_desarrollomovil.util.QuantityFormatter

/**
 * Alerta que aparece tras registrar una venta cuando uno o más productos
 * quedaron con poco stock o agotados. Usa la paleta de la app.
 */
@Composable
fun LowStockDialog(
    items: List<LowStockItem>,
    onDismiss: () -> Unit,
    confirmText: String = "Entendido"
) {
    if (items.isEmpty()) return

    val hasOut = items.any { it.isOut }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        tonalElevation = 8.dp,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(BrandOrange.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(BrandOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = if (hasOut) "¡Atención con tu inventario!" else "Stock bajo",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "La venta se registró correctamente. Estos productos necesitan que repongas stock:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                items.forEach { item ->
                    LowStockRow(item)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandOrange,
                    contentColor = Color.White
                )
            ) {
                Text(text = confirmText, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    )
}

@Composable
private fun LowStockRow(item: LowStockItem) {
    val accent = if (item.isOut) CancelRed else BrandOrange
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CreamBackground)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.productName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(accent.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (item.isOut) {
                    "Agotado"
                } else {
                    "Quedan ${QuantityFormatter.withUnit(item.remainingStock, item.unit)}"
                },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = accent
            )
        }
    }
}
