package com.example.practica_desarrollomovil.presentation.sales

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.clickable
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Search
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.practica_desarrollomovil.domain.model.Product
import com.example.practica_desarrollomovil.presentation.components.LowStockDialog
import com.example.practica_desarrollomovil.presentation.components.MetamercaAlertDialog
import com.example.practica_desarrollomovil.presentation.components.MetamercaSuccessDialog
import com.example.practica_desarrollomovil.presentation.components.MetamercaCard
import com.example.practica_desarrollomovil.presentation.components.MetamercaSnackbarHost
import com.example.practica_desarrollomovil.presentation.theme.BrandBrown
import com.example.practica_desarrollomovil.presentation.theme.BrandOrange
import com.example.practica_desarrollomovil.presentation.theme.CancelRed
import com.example.practica_desarrollomovil.presentation.theme.CreamBackground
import com.example.practica_desarrollomovil.presentation.theme.TextSecondary
import com.example.practica_desarrollomovil.util.CurrencyFormatter
import com.example.practica_desarrollomovil.util.QuantityFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterSaleScreen(
    viewModel: RegisterSaleViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showExitDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showLowStockDialog by remember { mutableStateOf(false) }
    // Si true, al cerrar la alerta de stock se sale de la pantalla; si false, se queda para otra venta.
    var exitAfterAlert by remember { mutableStateOf(true) }

    // Estados para el Modal de selección de productos
    var showProductModal by remember { mutableStateOf(false) }
    var currentLinePickingId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()

    val handleBack = {
        if (uiState.lineItems.any { it.productId != null }) {
            showExitDialog = true
        } else {
            onBack()
        }
    }

    BackHandler(onBack = handleBack)

    LaunchedEffect(uiState.savedSuccessfully) {
        if (uiState.savedSuccessfully) {
            showSuccessDialog = true
        }
    }

    // Tras cerrar el éxito: si hay productos con poco stock se muestra la alerta antes de continuar.
    val finishSuccess = { exit: Boolean ->
        showSuccessDialog = false
        viewModel.consumeSaveSuccess()
        if (uiState.lowStockItems.isNotEmpty()) {
            exitAfterAlert = exit
            showLowStockDialog = true
        } else if (exit) {
            onBack()
        }
    }

    if (showSuccessDialog) {
        MetamercaSuccessDialog(
            onDismissRequest = { finishSuccess(true) },
            onConfirm = { finishSuccess(true) },
            onSecondaryAction = { finishSuccess(false) },
            title = "¡Venta registrada!",
            text = "La venta se ha guardado correctamente en el sistema.",
            confirmText = "Aceptar",
            secondaryText = "Nueva venta"
        )
    }

    if (showLowStockDialog) {
        LowStockDialog(
            items = uiState.lowStockItems,
            onDismiss = {
                showLowStockDialog = false
                viewModel.consumeLowStockAlert()
                if (exitAfterAlert) onBack()
            }
        )
    }

    // Modal de Selección de Productos
    if (showProductModal) {
        ModalBottomSheet(
            onDismissRequest = { showProductModal = false; searchQuery = "" },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "Seleccione un producto",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BrandBrown
                )
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    placeholder = { Text("Buscar producto...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                
                val alreadySelectedIds = uiState.lineItems.mapNotNull { it.productId }.toSet()
                val filteredProducts = uiState.products.filter { 
                    it.name.contains(searchQuery, ignoreCase = true) && !alreadySelectedIds.contains(it.id)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    items(filteredProducts) { product ->
                        ProductSelectionItem(
                            product = product,
                            onClick = {
                                currentLinePickingId?.let { lineId ->
                                    viewModel.selectProduct(lineId, product.id)
                                }
                                showProductModal = false
                                searchQuery = ""
                            }
                        )
                    }
                }
            }
        }
    }

    if (showExitDialog) {
        MetamercaAlertDialog(
            onDismissRequest = { showExitDialog = false },
            onConfirm = onBack,
            title = "Cancelar venta",
            text = "¿Deseas cancelar el registro de esta venta? Se perderán los datos ingresados.",
            confirmText = "Salir",
            isDestructive = true
        )
    }

    if (showSaveDialog) {
        MetamercaAlertDialog(
            onDismissRequest = { showSaveDialog = false },
            onConfirm = {
                showSaveDialog = false
                viewModel.registerSale()
            },
            title = "Confirmar venta",
            text = "¿Estás seguro de registrar esta venta por un total de ${CurrencyFormatter.formatSoles(uiState.totalAmount)}?",
            confirmText = "Registrar"
        )
    }

    Scaffold(
        snackbarHost = { MetamercaSnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = BrandBrown)
                        Text(
                            text = if (uiState.receiptId == null) "Agregar venta" else "Editar venta #${uiState.receiptId}",
                            modifier = Modifier.padding(start = 8.dp),
                            color = BrandBrown,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = handleBack,
                        modifier = Modifier.semantics { contentDescription = "Volver a ventas" }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = BrandBrown)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CreamBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            MetamercaCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    uiState.lineItems.forEach { line ->
                        val selectedProduct = uiState.products.find { it.id == line.productId }
                        SaleLineRow(
                            line = line,
                            products = uiState.products,
                            availableStock = selectedProduct?.let { uiState.availableStockFor(it) },
                            isOverStock = uiState.isLineOverStock(line),
                            onPickProduct = {
                                currentLinePickingId = line.id
                                showProductModal = true
                            },
                            onQuantityChange = { qty -> viewModel.onQuantityChange(line.id, qty) },
                            onRemove = { viewModel.removeLineItem(line.id) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedButton(
                        onClick = viewModel::addLineItem,
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "Agregar otro producto a la venta" },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandBrown)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = BrandBrown)
                        Text(
                            text = " Agregar Producto",
                            color = BrandBrown,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CreamBackground)
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = CurrencyFormatter.formatSoles(uiState.totalAmount, withSign = true),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    uiState.errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = CancelRed,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        val hasOverStock = uiState.lineItems.any { uiState.isLineOverStock(it) }
                        Button(
                            onClick = { showSaveDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .semantics { contentDescription = "Guardar venta" },
                            enabled = !uiState.isSaving && !hasOverStock,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandBrown,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                            Text(
                                text = if (uiState.receiptId == null) " Guardar Venta" else " Guardar Cambios",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SaleLineRow(
    line: SaleLineItem,
    products: List<Product>,
    availableStock: Double?,
    isOverStock: Boolean,
    onPickProduct: () -> Unit,
    onQuantityChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    val selectedProduct = products.find { it.id == line.productId }
    val allowsDecimals = selectedProduct?.unit?.allowsDecimals ?: true

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = selectedProduct?.name ?: "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .weight(1.5f)
                    .clickable { onPickProduct() },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = if (selectedProduct != null) BrandBrown else TextSecondary,
                    disabledBorderColor = BrandBrown,
                    disabledPlaceholderColor = TextSecondary
                ),
                placeholder = {
                    Text(
                        "Seleccione producto",
                        maxLines = 1,
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = line.quantity,
                onValueChange = { newValue ->
                    // Enteros solo si la unidad no admite decimales.
                    val pattern = if (allowsDecimals) """^\d*\.?\d*$""" else """^\d*$"""
                    if (newValue.isEmpty() || newValue.matches(Regex(pattern))) {
                        onQuantityChange(newValue)
                    }
                },
                modifier = Modifier.weight(0.8f),
                isError = isOverStock,
                placeholder = { Text(if (allowsDecimals) "0.0" else "0") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (allowsDecimals) KeyboardType.Decimal else KeyboardType.Number
                ),
                suffix = {
                    selectedProduct?.let {
                        Text(it.unit.label, color = BrandBrown, style = MaterialTheme.typography.labelSmall)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CancelRed)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar item", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }

        // Texto de apoyo: stock disponible o aviso de que se excede.
        if (selectedProduct != null && availableStock != null) {
            val overColor = if (isOverStock) CancelRed else TextSecondary
            Text(
                text = if (isOverStock) {
                    "Solo hay ${QuantityFormatter.withUnit(availableStock, selectedProduct.unit)} disponibles"
                } else {
                    "Disponible: ${QuantityFormatter.withUnit(availableStock, selectedProduct.unit)}"
                },
                style = MaterialTheme.typography.labelSmall,
                color = overColor,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun ProductSelectionItem(
    product: Product,
    onClick: () -> Unit
) {
    val isOut = product.stock <= 0.0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isOut, onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CreamBackground),
            contentAlignment = Alignment.Center
        ) {
            if (product.imageUri != null) {
                AsyncImage(
                    model = product.imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = BrandBrown)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                color = if (isOut) TextSecondary else BrandBrown,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = if (isOut) "Agotado" else "Stock: ${QuantityFormatter.withUnit(product.stock, product.unit)}",
                style = MaterialTheme.typography.bodySmall,
                color = if (isOut) CancelRed else TextSecondary,
                fontWeight = if (isOut) FontWeight.Bold else FontWeight.Normal
            )
        }

        Text(
            text = CurrencyFormatter.formatSoles(product.pricePerUnit),
            style = MaterialTheme.typography.titleMedium,
            color = if (isOut) TextSecondary else BrandBrown,
            fontWeight = FontWeight.Bold
        )
    }
}
