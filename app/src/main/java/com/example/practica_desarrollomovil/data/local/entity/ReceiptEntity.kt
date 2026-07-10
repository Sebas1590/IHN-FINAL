package com.example.practica_desarrollomovil.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receipts")
data class ReceiptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val totalAmount: Double,
    val totalProfit: Double,
    val soldAtMillis: Long
)
