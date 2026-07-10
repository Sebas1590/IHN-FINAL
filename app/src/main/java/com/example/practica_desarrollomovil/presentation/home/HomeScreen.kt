package com.example.practica_desarrollomovil.presentation.home

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShowChart
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.practica_desarrollomovil.domain.model.ActivityType
import com.example.practica_desarrollomovil.domain.model.RecentActivity
import com.example.practica_desarrollomovil.presentation.components.MetamercaCard
import com.example.practica_desarrollomovil.presentation.components.MetamercaLogo
import com.example.practica_desarrollomovil.presentation.components.StatCard
import com.example.practica_desarrollomovil.presentation.theme.ActionBlue
import com.example.practica_desarrollomovil.presentation.theme.ActionGreen
import com.example.practica_desarrollomovil.presentation.theme.BrandBrown
import com.example.practica_desarrollomovil.presentation.theme.BrandOrange
import com.example.practica_desarrollomovil.presentation.theme.CreamBackground
import com.example.practica_desarrollomovil.presentation.theme.TextPrimary
import com.example.practica_desarrollomovil.presentation.theme.TextSecondary
import com.example.practica_desarrollomovil.util.CurrencyFormatter
import com.example.practica_desarrollomovil.util.DateTimeUtils

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onRegisterSale: () -> Unit,
    onAddProduct: () -> Unit,
    onNavigateToEarnings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val summary = uiState.summary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MetamercaLogo(compact = true)
        
        Text(
            text = DateTimeUtils.formatHeaderDate(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
            color = TextSecondary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "VENTAS HOY",
                value = summary.salesCountToday.toString(),
                icon = Icons.Default.ShoppingCart,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToEarnings() }
            )
            StatCard(
                title = "INGRESO HOY",
                value = CurrencyFormatter.formatSolesCompact(summary.incomeToday),
                icon = Icons.Default.ShowChart,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToEarnings() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        MetamercaCard(
            containerColor = BrandOrange,
            modifier = Modifier.clickable { onNavigateToEarnings() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = "GANANCIA NETA HOY",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = CurrencyFormatter.formatSolesCompact(summary.netProfitToday),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionTile(
                label = "Agregar Productos",
                icon = Icons.Default.Inventory2,
                color = ActionGreen,
                modifier = Modifier.weight(1f),
                onClick = onAddProduct
            )
            ActionTile(
                label = "Registrar Venta",
                icon = Icons.Default.Receipt,
                color = ActionBlue,
                modifier = Modifier.weight(1f),
                onClick = onRegisterSale
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        
        Text(
            text = "Actividad reciente",
            style = MaterialTheme.typography.titleMedium,
            color = BrandBrown,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp)
        )

        MetamercaCard {
            if (uiState.recentActivity.isEmpty()) {
                Text(
                    text = "Sin actividad aún. Agrega productos o registra una venta.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                uiState.recentActivity.forEachIndexed { index, activity ->
                    ActivityRow(activity)
                    if (index < uiState.recentActivity.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(horizontal = 16.dp)
                                .background(CreamBackground)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun ActionTile(
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ActivityRow(activity: RecentActivity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CreamBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (activity.type) {
                    ActivityType.SALE -> Icons.Default.Receipt
                    ActivityType.STOCK -> Icons.Default.Inventory2
                },
                contentDescription = null,
                tint = BrandBrown
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(text = activity.title, style = MaterialTheme.typography.titleMedium, color = BrandBrown)
            Text(text = activity.subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
        Text(
            text = activity.detail,
            color = BrandBrown,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
