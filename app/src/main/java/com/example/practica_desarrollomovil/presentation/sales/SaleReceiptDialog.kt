package com.example.practica_desarrollomovil.presentation.sales

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.practica_desarrollomovil.domain.model.Receipt
import com.example.practica_desarrollomovil.presentation.theme.BrandBrown
import com.example.practica_desarrollomovil.presentation.theme.TextSecondary
import com.example.practica_desarrollomovil.util.CurrencyFormatter
import com.example.practica_desarrollomovil.util.DateTimeUtils
import java.util.Locale

@Composable
fun SaleReceiptDialog(
    receipt: Receipt,
    onDismiss: () -> Unit,
    onEdit: (() -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "METAMERCA",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = BrandBrown
                )
                Text(
                    text = "Boleta de Venta",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                DashedLine()
                Spacer(modifier = Modifier.height(16.dp))
                
                ReceiptRow("Transacción:", "#${receipt.id}")
                ReceiptRow("Fecha:", DateTimeUtils.formatDateShort(receipt.soldAtMillis))
                ReceiptRow("Hora:", DateTimeUtils.formatTime(receipt.soldAtMillis))
                
                Spacer(modifier = Modifier.height(16.dp))
                DashedLine()
                Spacer(modifier = Modifier.height(16.dp))
                
                // Table Headers
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Cant.", modifier = Modifier.weight(0.7f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    Text("Descripción", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    Text("Total", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.End)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Items
                receipt.items.forEach { item ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                        Text(
                            text = String.format(Locale.US, "%.1f", item.quantity), 
                            modifier = Modifier.weight(0.7f), 
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = item.productName, 
                            modifier = Modifier.weight(2f), 
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = CurrencyFormatter.formatSoles(item.totalAmount),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.End
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                DashedLine()
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = CurrencyFormatter.formatSoles(receipt.totalAmount, withSign = true),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandBrown
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "¡Gracias por su compra!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (onEdit != null) {
                        OutlinedButton(
                            onClick = onEdit,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandBrown),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BrandBrown)
                        ) {
                            Text("Editar", fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandBrown,
                            contentColor = Color.White // Fix visibility
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cerrar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DashedLine() {
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = Color.LightGray,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect
        )
    }
}
