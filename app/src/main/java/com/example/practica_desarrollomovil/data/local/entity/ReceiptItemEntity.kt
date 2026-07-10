package com.example.practica_desarrollomovil.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "receipt_items",
    foreignKeys = [
        ForeignKey(
            entity = ReceiptEntity::class,
            parentColumns = ["id"],
            childColumns = ["receiptId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("receiptId"), Index("productId")]
)
data class ReceiptItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val receiptId: Long,
    val productId: Long?,
    val productName: String,
    val quantity: Double,
    val unitPrice: Double,
    val totalAmount: Double,
    val profitAmount: Double
)
