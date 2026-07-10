package com.example.practica_desarrollomovil.domain.repository

import com.example.practica_desarrollomovil.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observeAllProducts(): Flow<List<Product>>
    fun observeProduct(id: Long): Flow<Product?>
    suspend fun getProduct(id: Long): Product?
    suspend fun upsertProduct(product: Product): Long
    suspend fun deleteProduct(id: Long)
}
