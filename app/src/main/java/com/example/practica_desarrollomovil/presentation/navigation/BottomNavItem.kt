package com.example.practica_desarrollomovil.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    HOME(Routes.HOME, "Inicio", Icons.Default.Home),
    PRODUCTS(Routes.PRODUCTS, "Productos", Icons.Default.Inventory2),
    SALES(Routes.SALES, "Ventas", Icons.Default.Receipt),
    EARNINGS(Routes.EARNINGS, "Ganancias", Icons.Default.AttachMoney),
    ACCESSIBILITY(Routes.ACCESSIBILITY, "Perfil", Icons.Default.Person)
}
