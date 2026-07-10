package com.example.practica_desarrollomovil.presentation.sales

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practica_desarrollomovil.di.AppContainer
import com.example.practica_desarrollomovil.domain.model.Receipt
import com.example.practica_desarrollomovil.presentation.components.MetamercaCard
import com.example.practica_desarrollomovil.presentation.theme.BrandBrown
import com.example.practica_desarrollomovil.presentation.theme.CreamBackground
import com.example.practica_desarrollomovil.presentation.theme.TextSecondary
import com.example.practica_desarrollomovil.util.CurrencyFormatter
import com.example.practica_desarrollomovil.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    container: AppContainer,
    onRegisterSale: () -> Unit,
    onEditReceipt: (Long) -> Unit
) {
    val viewModel = viewModel<SalesListViewModel>(factory = container.salesListViewModelFactory())
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    var receiptToView by remember { mutableStateOf<Receipt?>(null) }
    
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    if (showFromPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = DateTimeUtils.parseDateShort(uiState.dateFrom)
        )
        DatePickerDialog(
            onDismissRequest = { showFromPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        viewModel.onDateFromChange(DateTimeUtils.formatDateShort(it))
                    }
                    showFromPicker = false
                }) {
                    Text("Aceptar", color = BrandBrown, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFromPicker = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showToPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = DateTimeUtils.parseDateShort(uiState.dateTo)
        )
        DatePickerDialog(
            onDismissRequest = { showToPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        viewModel.onDateToChange(DateTimeUtils.formatDateShort(it))
                    }
                    showToPicker = false
                }) {
                    Text("Aceptar", color = BrandBrown, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showToPicker = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    receiptToView?.let { receipt ->
        SaleReceiptDialog(
            receipt = receipt,
            onDismiss = { receiptToView = null },
            onEdit = {
                receiptToView = null
                onEditReceipt(receipt.id)
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = "Ventas",
                tint = BrandBrown,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = "Ventas",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
            Button(
                onClick = onRegisterSale,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandBrown,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "+ Nueva Venta", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                DateFilterField(
                    value = uiState.dateFrom,
                    label = "Desde",
                    onClick = { showFromPicker = true }
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                DateFilterField(
                    value = uiState.dateTo,
                    label = "Hasta",
                    onClick = { showToPicker = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.receipts.isEmpty()) {
            Text(
                text = "No hay ventas registradas para este periodo.",
                color = TextSecondary,
                modifier = Modifier.padding(top = 24.dp)
            )
        } else {
            LazyColumn {
                itemsIndexed(uiState.receipts, key = { _, receipt -> receipt.id }) { _, receipt ->
                    MetamercaCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Venta #${receipt.id}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${DateTimeUtils.formatDateShort(receipt.soldAtMillis)} - ${receipt.items.size} items",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = CurrencyFormatter.formatSoles(receipt.totalAmount, withSign = true),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrandBrown
                                )
                                IconButton(onClick = { receiptToView = receipt }) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = "Ver boleta",
                                        tint = BrandBrown
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun DateFilterField(
    value: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = BrandBrown) },
            trailingIcon = {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = BrandBrown)
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = BrandBrown,
                disabledBorderColor = BrandBrown,
                disabledLabelColor = BrandBrown,
                disabledTrailingIconColor = BrandBrown
            )
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { onClick() }
        )
    }
}
