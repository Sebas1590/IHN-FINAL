package com.example.practica_desarrollomovil.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.practica_desarrollomovil.domain.model.Product
import com.example.practica_desarrollomovil.domain.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProductsUiState(
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val filteredProducts: List<Product> = emptyList(),
    val message: String? = null
)

class ProductsViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val _message = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProductsUiState> = combine(
        productRepository.observeAllProducts(),
        searchQuery,
        _message
    ) { products, query, msg ->
        val filtered = if (query.isBlank()) {
            products
        } else {
            products.filter { it.name.contains(query, ignoreCase = true) }
        }
        ProductsUiState(
            products = products,
            searchQuery = query,
            filteredProducts = filtered,
            message = msg
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProductsUiState())

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            productRepository.deleteProduct(id)
            _message.value = "Producto eliminado correctamente"
        }
    }

    fun consumeMessage() {
        _message.value = null
    }

    class Factory(
        private val productRepository: ProductRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProductsViewModel(productRepository) as T
        }
    }
}
