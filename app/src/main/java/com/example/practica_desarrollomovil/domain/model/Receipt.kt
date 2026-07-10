package com.example.practica_desarrollomovil.domain.model

data class Receipt(
    val id: Long = 0L,
    val items: List<ReceiptItem>,
    val totalAmount: Double,
    val totalProfit: Double,
    val soldAtMillis: Long = System.currentTimeMillis()
)

data class ReceiptItem(
    val id: Long = 0L,
    val productId: Long?,
    val productName: String,
    val quantity: Double,
    val unitPrice: Double,
    val totalAmount: Double,
    val profitAmount: Double
)
