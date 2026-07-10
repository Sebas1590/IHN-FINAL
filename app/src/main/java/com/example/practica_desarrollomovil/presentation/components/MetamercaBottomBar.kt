package com.example.practica_desarrollomovil.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practica_desarrollomovil.presentation.navigation.BottomNavItem
import com.example.practica_desarrollomovil.presentation.theme.BottomNavHighlight
import com.example.practica_desarrollomovil.presentation.theme.BrandBrown
import com.example.practica_desarrollomovil.presentation.theme.CardWhite
import com.example.practica_desarrollomovil.presentation.theme.NavInactive
import com.example.practica_desarrollomovil.presentation.theme.TextPrimary

@Composable
fun MetamercaBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(containerColor = CardWhite) {
        BottomNavItem.entries.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    if (selected) {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BottomNavHighlight),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Icon(item.icon, contentDescription = item.label, tint = BrandBrown)
                        }
                    } else {
                        Icon(item.icon, contentDescription = item.label, tint = NavInactive)
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        color = if (selected) BrandBrown else NavInactive
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
