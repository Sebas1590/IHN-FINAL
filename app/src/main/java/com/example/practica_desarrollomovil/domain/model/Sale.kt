package com.example.practica_desarrollomovil.domain.model

data class Sale(
    val id: Long = 0L,
    val productId: Long?,
    val productName: String,
    val quantity: Double,
    val unitPrice: Double,
    val totalAmount: Double,
    val profitAmount: Double,
    val soldAtMillis: Long = System.currentTimeMillis()
)
