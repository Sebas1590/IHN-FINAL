package com.example.practica_desarrollomovil.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.practica_desarrollomovil.domain.model.Product
import com.example.practica_desarrollomovil.domain.model.ProductUnit
import com.example.practica_desarrollomovil.domain.repository.ProductRepository
import com.example.practica_desarrollomovil.domain.repository.SaleRepository
import com.example.practica_desarrollomovil.util.QuantityFormatter
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class InvestmentMode {
    TOTAL, UNIT_COST, NONE
}

data class ProductFormUiState(
    val productId: Long? = null,
    val name: String = "",
    val stock: String = "",
    val unit: ProductUnit = ProductUnit.UNID,
    val pricePerUnit: String = "",
    val investmentMode: InvestmentMode = InvestmentMode.TOTAL,
    val unitCost: String = "",
    val totalInvestment: String = "",
    val imageUri: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val hasSales: Boolean = false,
    val errorMessage: String? = null,
    val savedSuccessfully: Boolean = false
)

class ProductFormViewModel(
    private val productRepository: ProductRepository,
    private val saleRepository: SaleRepository,
    productId: Long?
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductFormUiState(productId = productId))
    val uiState: StateFlow<ProductFormUiState> = _uiState.asStateFlow()

    init {
        productId?.let { loadProduct(it) }
    }

    private fun loadProduct(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val product = productRepository.getProduct(id)
            val hasSales = saleRepository.hasSalesForProduct(id)
            if (product != null) {
                _uiState.update {
                    it.copy(
                        name = product.name,
                        stock = QuantityFormatter.toInput(product.stock, product.unit),
                        unit = product.unit,
                        pricePerUnit = product.pricePerUnit.toString(),
                        totalInvestment = product.totalInvestment.toString(),
                        imageUri = product.imageUri,
                        hasSales = hasSales,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Producto no encontrado")
                }
            }
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    
    fun onStockChange(value: String) = _uiState.update {
        if (!it.unit.allowsDecimals && (value.contains(".") || value.contains(","))) return@update it

        val newStock = value.toDoubleOrNull() ?: 0.0
        val currentTotal = it.totalInvestment.toDoubleOrNull() ?: 0.0
        val currentUnitCost = it.unitCost.toDoubleOrNull() ?: 0.0
        
        val updatedUnitCost = if (it.investmentMode == InvestmentMode.TOTAL && newStock > 0) {
            String.format(Locale.US, "%.2f", currentTotal / newStock)
        } else it.unitCost

        val updatedTotal = if (it.investmentMode == InvestmentMode.UNIT_COST) {
            String.format(Locale.US, "%.2f", currentUnitCost * newStock)
        } else it.totalInvestment

        it.copy(stock = value, unitCost = updatedUnitCost, totalInvestment = updatedTotal) 
    }

    fun onUnitChange(unit: ProductUnit) = _uiState.update {
        // Al pasar a una unidad entera, se recorta cualquier decimal ya escrito en el stock.
        val sanitizedStock = if (!unit.allowsDecimals) it.stock.substringBefore(".").substringBefore(",") else it.stock
        it.copy(unit = unit, stock = sanitizedStock)
    }
    fun onPriceChange(value: String) = _uiState.update { it.copy(pricePerUnit = value) }
    
    fun onInvestmentModeChange(mode: InvestmentMode) = _uiState.update { it.copy(investmentMode = mode) }
    
    fun onUnitCostChange(value: String) = _uiState.update { 
        val unitCost = value.toDoubleOrNull() ?: 0.0
        val stock = it.stock.toDoubleOrNull() ?: 0.0
        val total = unitCost * stock
        it.copy(
            unitCost = value,
            totalInvestment = if (it.investmentMode == InvestmentMode.UNIT_COST) String.format(Locale.US, "%.2f", total) else it.totalInvestment
        )
    }

    fun onTotalInvestmentChange(value: String) = _uiState.update { 
        val total = value.toDoubleOrNull() ?: 0.0
        val stock = it.stock.toDoubleOrNull() ?: 0.0
        val unitCost = if (stock > 0) total / stock else 0.0
        it.copy(
            totalInvestment = value,
            unitCost = if (it.investmentMode == InvestmentMode.TOTAL) String.format(Locale.US, "%.2f", unitCost) else it.unitCost
        )
    }
    
    fun onImageUriChange(uri: String?) = _uiState.update { it.copy(imageUri = uri) }

    fun saveProduct() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingresa el nombre del producto") }
            return
        }

        val stock = state.stock.toDoubleOrNull() ?: 0.0
        val price = state.pricePerUnit.toDoubleOrNull() ?: 0.0
        
        val finalInvestment = when (state.investmentMode) {
            InvestmentMode.TOTAL -> state.totalInvestment.toDoubleOrNull() ?: 0.0
            InvestmentMode.UNIT_COST -> {
                val cost = state.unitCost.toDoubleOrNull() ?: 0.0
                cost * stock
            }
            InvestmentMode.NONE -> 0.0
        }

        if (stock < 0) {
            _uiState.update { it.copy(errorMessage = "Stock inválido") }
            return
        }
        if (price < 0) {
            _uiState.update { it.copy(errorMessage = "Precio inválido") }
            return
        }

        // Validación: las unidades enteras no permiten decimales
        if (!state.unit.allowsDecimals && stock % 1.0 != 0.0) {
            _uiState.update { it.copy(errorMessage = "La unidad '${state.unit.label}' no permite decimales") }
            return
        }

        val isNew = state.productId == null
        val shouldValidate = isNew || !state.hasSales

        if (shouldValidate) {
            val totalRevenue = price * stock
            if (finalInvestment > totalRevenue && stock > 0) {
                _uiState.update { 
                    it.copy(errorMessage = "La inversión total (S/ ${String.format(Locale.getDefault(), "%.2f", finalInvestment)}) no puede ser mayor a la venta proyectada (S/ ${String.format(Locale.getDefault(), "%.2f", totalRevenue)})")
                }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                productRepository.upsertProduct(
                    Product(
                        id = state.productId ?: 0L,
                        name = state.name,
                        stock = stock,
                        unit = state.unit,
                        pricePerUnit = price,
                        totalInvestment = finalInvestment,
                        imageUri = state.imageUri
                    )
                )
                _uiState.update { it.copy(isSaving = false, savedSuccessfully = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = e.message ?: "Error al guardar")
                }
            }
        }
    }

    fun consumeSaveSuccess() {
        _uiState.update { it.copy(savedSuccessfully = false) }
    }

    class Factory(
        private val productRepository: ProductRepository,
        private val saleRepository: SaleRepository,
        private val productId: Long?
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProductFormViewModel(productRepository, saleRepository, productId) as T
        }
    }
}
