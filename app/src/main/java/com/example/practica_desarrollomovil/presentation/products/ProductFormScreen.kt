package com.example.practica_desarrollomovil.presentation.products

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext
import com.example.practica_desarrollomovil.util.FileUtils
import com.example.practica_desarrollomovil.presentation.components.MetamercaSuccessDialog
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import com.example.practica_desarrollomovil.presentation.products.InvestmentMode
import com.example.practica_desarrollomovil.domain.model.ProductUnit
import com.example.practica_desarrollomovil.presentation.components.MetamercaAlertDialog
import com.example.practica_desarrollomovil.presentation.components.MetamercaSnackbarHost
import com.example.practica_desarrollomovil.presentation.theme.BrandBrown
import com.example.practica_desarrollomovil.presentation.theme.CancelRed
import com.example.practica_desarrollomovil.presentation.theme.CreamBackground
import com.example.practica_desarrollomovil.presentation.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    viewModel: ProductFormViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val localPath = FileUtils.saveImageToInternalStorage(context, it)
            viewModel.onImageUriChange(localPath)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val uri = try {
                val file = java.io.File(context.filesDir, "product_images").apply { if (!exists()) mkdirs() }
                val targetFile = java.io.File(file, "cam_${java.util.UUID.randomUUID()}.jpg")
                java.io.FileOutputStream(targetFile).use { out ->
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
                }
                Uri.fromFile(targetFile).toString()
            } catch (e: Exception) { null }
            viewModel.onImageUriChange(uri)
        }
    }

    var showImageSourceDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showExitDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val handleBack = {
        if (uiState.name.isNotEmpty() || uiState.pricePerUnit.isNotEmpty()) {
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

    if (showSuccessDialog) {
        MetamercaSuccessDialog(
            onDismissRequest = {
                showSuccessDialog = false
                viewModel.consumeSaveSuccess()
                onBack()
            },
            onConfirm = {
                showSuccessDialog = false
                viewModel.consumeSaveSuccess()
                onBack()
            },
            onSecondaryAction = {
                showSuccessDialog = false
                viewModel.consumeSaveSuccess()
                // Al no hacer onBack(), el usuario se queda en la pantalla.
                // Como el viewModel limpia el estado al resetear (si lo implementamos), se queda listo.
            },
            title = "¡Producto registrado!",
            text = "Tu artículo ya está disponible en el inventario.",
            confirmText = "Aceptar",
            secondaryText = "Agregar nuevo producto"
        )
    }

    if (showImageSourceDialog) {
        MetamercaAlertDialog(
            onDismissRequest = {
                showImageSourceDialog = false
                imagePicker.launch("image/*")
            },
            onConfirm = {
                showImageSourceDialog = false
                cameraLauncher.launch(null)
            },
            title = "Seleccionar imagen",
            text = "¿Deseas tomar una foto o elegir una de la galería?",
            confirmText = "Cámara",
            dismissText = "Galería"
        )
    }

    if (showExitDialog) {
        MetamercaAlertDialog(
            onDismissRequest = { showExitDialog = false },
            onConfirm = onBack,
            title = "Salir sin guardar",
            text = "¿Estás seguro de que deseas salir? Se perderán todos los cambios no guardados.",
            confirmText = "Salir",
            isDestructive = true
        )
    }

    if (showSaveDialog) {
        MetamercaAlertDialog(
            onDismissRequest = { showSaveDialog = false },
            onConfirm = {
                showSaveDialog = false
                viewModel.saveProduct()
            },
            title = "Guardar cambios",
            text = "¿Deseas guardar los cambios realizados en este producto?",
            confirmText = "Guardar"
        )
    }

    Scaffold(
        snackbarHost = { MetamercaSnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, tint = BrandBrown)
                        Text(
                            text = if (uiState.productId == null) "Agregar producto" else "Editar producto",
                            modifier = Modifier.padding(start = 8.dp),
                            color = BrandBrown,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = handleBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            Text("Nombre del Producto", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp),
                placeholder = { Text("Ej. Leche Gloria") },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Stock", style = MaterialTheme.typography.labelLarge)
                    OutlinedTextField(
                        value = uiState.stock,
                        onValueChange = {
                            val pattern = if (uiState.unit.allowsDecimals) """^\d*\.?\d*$""" else """^\d*$"""
                            if (it.isEmpty() || it.matches(Regex(pattern))) viewModel.onStockChange(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = if (uiState.unit.allowsDecimals) KeyboardType.Decimal else KeyboardType.Number
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    UnitDropdown(
                        selected = uiState.unit,
                        onSelected = viewModel::onUnitChange
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Precio de Venta x ${uiState.unit.label}", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = uiState.pricePerUnit,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("""^\d*\.?\d*$"""))) viewModel.onPriceChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp),
                prefix = { Text("S/ ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Text("Inversión / Costo de Compra", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                val modes = listOf(InvestmentMode.TOTAL, InvestmentMode.UNIT_COST, InvestmentMode.NONE)
                val labels = listOf("Total", "X Unidad", "Ninguna")
                
                modes.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = uiState.investmentMode == mode,
                        onClick = { viewModel.onInvestmentModeChange(mode) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size),
                        label = { Text(labels[index], style = MaterialTheme.typography.labelSmall) },
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = BrandBrown,
                            activeContentColor = Color.White,
                            inactiveContentColor = BrandBrown
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (uiState.investmentMode) {
                InvestmentMode.TOTAL -> {
                    OutlinedTextField(
                        value = uiState.totalInvestment,
                        onValueChange = { if (it.isEmpty() || it.matches(Regex("""^\d*\.?\d*$"""))) viewModel.onTotalInvestmentChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Inversión Total") },
                        prefix = { Text("S/ ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
                InvestmentMode.UNIT_COST -> {
                    OutlinedTextField(
                        value = uiState.unitCost,
                        onValueChange = { if (it.isEmpty() || it.matches(Regex("""^\d*\.?\d*$"""))) viewModel.onUnitCostChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Costo de Compra x ${uiState.unit.label}") },
                        prefix = { Text("S/ ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
                InvestmentMode.NONE -> {
                    Text(
                        "No se registrará costo de inversión para este producto.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Subir Imagen", style = MaterialTheme.typography.labelLarge)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(top = 8.dp, bottom = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, TextSecondary, RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .clickable { showImageSourceDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.imageUri != null) {
                    AsyncImage(
                        model = uiState.imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = TextSecondary)
                        Text(
                            text = "Subir una foto del producto",
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            uiState.errorMessage?.let { error ->
                Text(text = error, color = CancelRed, modifier = Modifier.padding(bottom = 8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showSaveDialog = true },
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isSaving,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandBrown,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                    Text(text = " Guardar", color = Color.White, modifier = Modifier.padding(start = 4.dp))
                }
                Button(
                    onClick = handleBack,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CancelRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                    Text(text = " Cancelar", color = Color.White, modifier = Modifier.padding(start = 4.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitDropdown(
    selected: ProductUnit,
    onSelected: (ProductUnit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Text("Unidad", style = MaterialTheme.typography.labelLarge)
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        OutlinedTextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ProductUnit.entries.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit.label) },
                    onClick = {
                        onSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}
