package com.example.practica_desarrollomovil.presentation.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.practica_desarrollomovil.domain.model.Product
import com.example.practica_desarrollomovil.presentation.components.MetamercaAlertDialog
import com.example.practica_desarrollomovil.presentation.components.MetamercaCard
import com.example.practica_desarrollomovil.presentation.components.MetamercaSnackbarHost
import com.example.practica_desarrollomovil.presentation.theme.BrandBrown
import com.example.practica_desarrollomovil.presentation.theme.CancelRed
import com.example.practica_desarrollomovil.presentation.theme.CreamBackground
import com.example.practica_desarrollomovil.presentation.theme.TextSecondary
import com.example.practica_desarrollomovil.util.CurrencyFormatter

@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel,
    onAddProduct: () -> Unit,
    onEditProduct: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var productToDeleteId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    if (productToDeleteId != null) {
        MetamercaAlertDialog(
            onDismissRequest = { productToDeleteId = null },
            onConfirm = {
                productToDeleteId?.let { viewModel.deleteProduct(it) }
                productToDeleteId = null
            },
            title = "Eliminar Producto",
            text = "¿Estás seguro de que deseas eliminar este producto? Esta acción no se puede deshacer.",
            confirmText = "Eliminar",
            isDestructive = true
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp) // Alineación unificada con Ventas y Home
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory2,
                    contentDescription = null,
                    tint = BrandBrown,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "Productos",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
                Button(
                    onClick = onAddProduct,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandBrown,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                    Text(
                        text = " Nuevo",
                        color = Color.White,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                placeholder = { Text("Buscar productos...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )

            if (uiState.filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (uiState.products.isEmpty()) {
                            "No hay productos. Pulsa + Nuevo Producto."
                        } else {
                            "No se encontraron productos."
                        },
                        color = TextSecondary
                    )
                }
            } else {
                LazyColumn {
                    items(uiState.filteredProducts, key = { it.id }) { product ->
                        ProductListItem(
                            product = product,
                            onEdit = { onEditProduct(product.id) },
                            onDelete = { productToDeleteId = product.id }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        ) {
            MetamercaSnackbarHost(hostState = snackbarHostState)
        }
    }
}

@Composable
private fun ProductListItem(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    MetamercaCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CreamBackground),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageUri != null) {
                    AsyncImage(
                        model = product.imageUri,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("📦")
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = CurrencyFormatter.formatSoles(product.pricePerUnit),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = BrandBrown)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = CancelRed)
            }
        }
    }
}
