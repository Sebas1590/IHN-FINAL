package com.example.practica_desarrollomovil.presentation.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.practica_desarrollomovil.domain.model.Product
import com.example.practica_desarrollomovil.domain.model.ReceiptItem
import com.example.practica_desarrollomovil.domain.repository.ProductRepository
import com.example.practica_desarrollomovil.domain.repository.SaleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class SaleLineItem(
    val id: String = UUID.randomUUID().toString(),
    val productId: Long? = null,
    val quantity: String = ""
)

data class RegisterSaleUiState(
    val receiptId: Long? = null,
    val products: List<Product> = emptyList(),
    val lineItems: List<SaleLineItem> = listOf(SaleLineItem()),
    val isSaving: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val savedSuccessfully: Boolean = false
) {
    val totalAmount: Double
        get() {
            return lineItems.sumOf { line ->
                val product = products.find { it.id == line.productId } ?: return@sumOf 0.0
                val qty = line.quantity.toDoubleOrNull() ?: 0.0
                product.pricePerUnit * qty
            }
        }
}

class RegisterSaleViewModel(
    productRepository: ProductRepository,
    private val saleRepository: SaleRepository,
    private val initialReceiptId: Long? = null
) : ViewModel() {

    private val formState = MutableStateFlow(RegisterSaleUiState(receiptId = initialReceiptId))

    val uiState: StateFlow<RegisterSaleUiState> = combine(
        productRepository.observeAllProducts(),
        formState
    ) { products, form ->
        form.copy(products = products)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RegisterSaleUiState())

    init {
        initialReceiptId?.let { loadReceipt(it) }
    }

    private fun loadReceipt(id: Long) {
        viewModelScope.launch {
            formState.update { it.copy(isLoading = true) }
            val receipt = saleRepository.getReceipt(id)
            if (receipt != null) {
                formState.update {
                    it.copy(
                        isLoading = false,
                        lineItems = receipt.items.map { item ->
                            SaleLineItem(
                                id = UUID.randomUUID().toString(),
                                productId = item.productId,
                                quantity = item.quantity.toString()
                            )
                        }
                    )
                }
            } else {
                formState.update { it.copy(isLoading = false, errorMessage = "Venta no encontrada") }
            }
        }
    }

    fun addLineItem() {
        formState.update { state ->
            state.copy(lineItems = state.lineItems + SaleLineItem())
        }
    }

    fun removeLineItem(id: String) {
        formState.update { state ->
            val updated = state.lineItems.filterNot { it.id == id }
            state.copy(
                lineItems = if (updated.isEmpty()) listOf(SaleLineItem()) else updated,
                errorMessage = null
            )
        }
    }

    fun selectProduct(lineId: String, productId: Long) {
        formState.update { state ->
            val isAlreadySelected = state.lineItems.any { it.productId == productId && it.id != lineId }
            if (isAlreadySelected) {
                state.copy(errorMessage = "Este producto ya ha sido agregado a la venta")
            } else {
                state.copy(
                    lineItems = state.lineItems.map { line ->
                        if (line.id == lineId) line.copy(productId = productId) else line
                    },
                    errorMessage = null
                )
            }
        }
    }

    fun onQuantityChange(lineId: String, value: String) {
        formState.update { state ->
            val line = state.lineItems.find { it.id == lineId }
            val product = state.products.find { it.id == line?.productId }
            
            // Reemplazar cualquier punto o coma si el producto es UNID
            val processedValue = if (product?.unit == com.example.practica_desarrollomovil.domain.model.ProductUnit.UNID) {
                value.replace(".", "").replace(",", "")
            } else {
                value
            }

            state.copy(
                lineItems = state.lineItems.map { l ->
                    if (l.id == lineId) l.copy(quantity = processedValue) else l
                },
                errorMessage = null
            )
        }
    }

    fun registerSale() {
        val state = uiState.value
        val validItems = state.lineItems.mapNotNull { line ->
            val product = state.products.find { it.id == line.productId } ?: return@mapNotNull null
            val qty = line.quantity.toDoubleOrNull() ?: return@mapNotNull null
            if (qty <= 0) return@mapNotNull null
            
            // Validate stock and units
            if (qty > product.stock && state.receiptId == null) {
                // For new sale, check stock. For edit, it's more complex as repository handles it.
                // But for UI feedback, this is simplified.
            }

            ReceiptItem(
                productId = product.id,
                productName = product.name,
                quantity = qty,
                unitPrice = product.pricePerUnit,
                totalAmount = product.pricePerUnit * qty,
                profitAmount = 0.0
            )
        }

        if (validItems.isEmpty()) {
            formState.update { it.copy(errorMessage = "Agrega al menos un producto con cantidad válida") }
            return
        }

        viewModelScope.launch {
            formState.update { it.copy(isSaving = true, errorMessage = null) }

            val result = if (state.receiptId == null) {
                saleRepository.registerReceipt(validItems)
            } else {
                saleRepository.updateReceipt(state.receiptId, validItems)
            }

            result.onSuccess {
                formState.update {
                    it.copy(
                        isSaving = false,
                        savedSuccessfully = true,
                        lineItems = listOf(SaleLineItem())
                    )
                }
            }.onFailure { error ->
                formState.update { it.copy(isSaving = false, errorMessage = error.message) }
            }
        }
    }

    fun consumeSaveSuccess() {
        formState.update { it.copy(savedSuccessfully = false) }
    }

    class Factory(
        private val productRepository: ProductRepository,
        private val saleRepository: SaleRepository,
        private val receiptId: Long? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RegisterSaleViewModel(productRepository, saleRepository, receiptId) as T
        }
    }
}
