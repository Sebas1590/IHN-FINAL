package com.example.practica_desarrollomovil.presentation.earnings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.practica_desarrollomovil.presentation.components.MetamercaCard
import com.example.practica_desarrollomovil.presentation.theme.*
import com.example.practica_desarrollomovil.util.CurrencyFormatter
import com.example.practica_desarrollomovil.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarningsScreen(viewModel: EarningsViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    if (showFromPicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.dateFromMillis)
        DatePickerDialog(
            onDismissRequest = { showFromPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDateRangeSelected(datePickerState.selectedDateMillis, uiState.dateToMillis)
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
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.dateToMillis)
        DatePickerDialog(
            onDismissRequest = { showToPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDateRangeSelected(uiState.dateFromMillis, datePickerState.selectedDateMillis)
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header (Current)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Payments,
                contentDescription = null,
                tint = BrandBrown,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = "Ganancias",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = BrandBrown,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Text(
            text = "Análisis de rentabilidad y ventas",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Tab Selector
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            val tabs = EarningsTab.entries
            val labels = listOf("Semana", "Mes", "Fechas")
            tabs.forEachIndexed { index, tab ->
                SegmentedButton(
                    selected = uiState.selectedTab == tab,
                    onClick = { viewModel.onTabSelected(tab) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size),
                    label = { Text(labels[index], fontSize = 13.sp) },
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = BrandOrange,
                        activeContentColor = Color.White,
                        inactiveContainerColor = Color.White,
                        inactiveContentColor = TextSecondary
                    )
                )
            }
        }

        // Custom Date Range Inputs (Only for FECHAS tab)
        if (uiState.selectedTab == EarningsTab.FECHAS) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DateFilterField(
                    value = uiState.dateFromMillis?.let { DateTimeUtils.formatDateShort(it) } ?: "Desde",
                    label = "DESDE",
                    onClick = { showFromPicker = true },
                    modifier = Modifier.weight(1f)
                )
                DateFilterField(
                    value = uiState.dateToMillis?.let { DateTimeUtils.formatDateShort(it) } ?: "Hasta",
                    label = "HASTA",
                    onClick = { showToPicker = true },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Main Earnings Card
        MetamercaCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Balance Total Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "BALANCE TOTAL",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = CurrencyFormatter.formatSoles(uiState.totalSales),
                            style = MaterialTheme.typography.headlineMedium,
                            color = BrandBrown,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp).semantics {
                                contentDescription = "Balance total de ventas: ${CurrencyFormatter.formatSoles(uiState.totalSales)}"
                            }
                        )
                    }
                    // Legend
                    Column(horizontalAlignment = Alignment.Start) {
                        LegendItem(color = BrandBrown, label = "Ganancia")
                        Spacer(modifier = Modifier.height(4.dp))
                        LegendItem(color = BrandOrange.copy(alpha = 0.6f), label = "Inversión")
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Chart Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Guidelines
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        repeat(4) {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.LightGray.copy(alpha = 0.3f)))
                        }
                    }

                    // Bars
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val maxVal = (uiState.chartData.maxOfOrNull { it.profit + it.investment } ?: 1.0).coerceAtLeast(1.0)
                        
                        uiState.chartData.forEach { data ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // Investment Bar (Peach)
                                    Box(
                                        modifier = Modifier
                                            .width(if (uiState.selectedTab == EarningsTab.SEMANA) 10.dp else 30.dp)
                                            .fillMaxHeight((data.investment / maxVal).toFloat())
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(BrandOrange.copy(alpha = 0.6f))
                                    )
                                    // Profit Bar (Brown)
                                    Box(
                                        modifier = Modifier
                                            .width(if (uiState.selectedTab == EarningsTab.SEMANA) 10.dp else 30.dp)
                                            .fillMaxHeight((data.profit / maxVal).toFloat())
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(BrandBrown)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = data.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                // Range Label for FECHAS tab below chart
                val dateFrom = uiState.dateFromMillis
                val dateTo = uiState.dateToMillis
                if (uiState.selectedTab == EarningsTab.FECHAS && dateFrom != null && dateTo != null) {
                    Text(
                        text = DateTimeUtils.formatDateRange(dateFrom, dateTo),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(20.dp))

                // Summary Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryColumn(label = "VENTAS", value = CurrencyFormatter.formatSoles(uiState.totalSales))
                    SummaryColumn(label = "INVERSIÓN", value = CurrencyFormatter.formatSoles(uiState.totalInvestment))
                    SummaryColumn(label = "GANANCIA", value = CurrencyFormatter.formatSoles(uiState.totalProfit), valueColor = BrandOrange)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

@Composable
private fun SummaryColumn(label: String, value: String, valueColor: Color = TextPrimary) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontWeight = FontWeight.Bold)
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = valueColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun DateFilterField(
    value: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MetamercaCard(
        modifier = modifier.clickable { onClick() },
        containerColor = Color.White
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = BrandBrown, modifier = Modifier.size(18.dp))
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Text(text = value, style = MaterialTheme.typography.bodyMedium, color = BrandBrown, fontWeight = FontWeight.Bold)
            }
        }
    }
}
