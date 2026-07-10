package com.example.practica_desarrollomovil.presentation.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.practica_desarrollomovil.domain.model.LowStockItem
import com.example.practica_desarrollomovil.domain.model.Product
import com.example.practica_desarrollomovil.domain.model.ReceiptItem
import com.example.practica_desarrollomovil.domain.model.StockRules
import com.example.practica_desarrollomovil.domain.repository.ProductRepository
import com.example.practica_desarrollomovil.domain.repository.SaleRepository
import com.example.practica_desarrollomovil.util.QuantityFormatter
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
    /** Cantidad original por producto en el recibo que se está editando (para calcular stock disponible). */
    val originalQuantities: Map<Long, Double> = emptyMap(),
    val isSaving: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val savedSuccessfully: Boolean = false,
    val lowStockItems: List<LowStockItem> = emptyList()
) {
    val totalAmount: Double
        get() {
            return lineItems.sumOf { line ->
                val product = products.find { it.id == line.productId } ?: return@sumOf 0.0
                val qty = line.quantity.toDoubleOrNull() ?: 0.0
                product.pricePerUnit * qty
            }
        }

    /** Stock realmente disponible para un producto (suma lo ya reservado en el recibo en edición). */
    fun availableStockFor(product: Product): Double =
        product.stock + (originalQuantities[product.id] ?: 0.0)

    /** True si la línea seleccionó un producto y la cantidad supera el stock disponible. */
    fun isLineOverStock(line: SaleLineItem): Boolean {
        val product = products.find { it.id == line.productId } ?: return false
        val qty = line.quantity.toDoubleOrNull() ?: return false
        return qty > availableStockFor(product)
    }
}

class RegisterSaleViewModel(
    private val productRepository: ProductRepository,
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
                // Cantidad original por producto: permite editar sin bloquearse por el propio stock ya descontado.
                val originals = receipt.items
                    .mapNotNull { item -> item.productId?.let { pid -> pid to item.quantity } }
                    .groupBy({ it.first }, { it.second })
                    .mapValues { entry -> entry.value.sum() }

                formState.update {
                    it.copy(
                        isLoading = false,
                        originalQuantities = originals,
                        lineItems = receipt.items.map { item ->
                            SaleLineItem(
                                id = UUID.randomUUID().toString(),
                                productId = item.productId,
                                quantity = formatLoadedQuantity(item.productId, item.quantity)
                            )
                        }
                    )
                }
            } else {
                formState.update { it.copy(isLoading = false, errorMessage = "Venta no encontrada") }
            }
        }
    }

    private fun formatLoadedQuantity(productId: Long?, quantity: Double): String {
        val unit = uiState.value.products.find { it.id == productId }?.unit
        return if (unit != null) QuantityFormatter.toInput(quantity, unit) else QuantityFormatter.format(quantity)
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
                        // Al cambiar de producto se limpia la cantidad para no arrastrar valores
                        // inválidos (p. ej. decimales de un producto por kg a uno por unidad).
                        if (line.id == lineId) line.copy(productId = productId, quantity = "") else line
                    },
                    errorMessage = null
                )
            }
        }
    }

    fun onQuantityChange(lineId: String, value: String) {
        formState.update { state ->
            val line = state.lineItems.find { it.id == lineId }
            val product = uiState.value.products.find { it.id == line?.productId }

            // Si la unidad no admite decimales, se descartan puntos y comas.
            val processedValue = if (product != null && !product.unit.allowsDecimals) {
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

        // Construir líneas válidas y validar cada una con mensajes claros.
        val validItems = mutableListOf<ReceiptItem>()
        for (line in state.lineItems) {
            val product = state.products.find { it.id == line.productId } ?: continue
            val qty = line.quantity.toDoubleOrNull()

            if (qty == null || qty <= 0) {
                formState.update {
                    it.copy(errorMessage = "Ingresa una cantidad válida para ${product.name}")
                }
                return
            }

            if (!product.unit.allowsDecimals && qty % 1.0 != 0.0) {
                formState.update {
                    it.copy(errorMessage = "${product.name} se vende por unidad: usa cantidades enteras")
                }
                return
            }

            val available = state.availableStockFor(product)
            if (qty > available) {
                formState.update {
                    it.copy(
                        errorMessage = "No hay suficiente stock de ${product.name}. " +
                            "Disponible: ${QuantityFormatter.withUnit(available, product.unit)}"
                    )
                }
                return
            }

            validItems.add(
                ReceiptItem(
                    productId = product.id,
                    productName = product.name,
                    quantity = qty,
                    unitPrice = product.pricePerUnit,
                    totalAmount = product.pricePerUnit * qty,
                    profitAmount = 0.0
                )
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
                val lowStock = computeLowStock(validItems.mapNotNull { it.productId }.distinct())
                formState.update {
                    it.copy(
                        isSaving = false,
                        savedSuccessfully = true,
                        lowStockItems = lowStock,
                        originalQuantities = emptyMap(),
                        lineItems = listOf(SaleLineItem())
                    )
                }
            }.onFailure { error ->
                formState.update {
                    it.copy(isSaving = false, errorMessage = error.message ?: "No se pudo registrar la venta")
                }
            }
        }
    }

    /** Relee el stock actualizado y arma la lista de productos con poco stock o agotados. */
    private suspend fun computeLowStock(productIds: List<Long>): List<LowStockItem> =
        productIds.mapNotNull { id ->
            val product = productRepository.getProduct(id) ?: return@mapNotNull null
            if (StockRules.needsAlert(product.stock, product.unit)) {
                LowStockItem(
                    productName = product.name,
                    remainingStock = product.stock,
                    unit = product.unit
                )
            } else null
        }

    fun consumeSaveSuccess() {
        formState.update { it.copy(savedSuccessfully = false) }
    }

    fun consumeLowStockAlert() {
        formState.update { it.copy(lowStockItems = emptyList()) }
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
