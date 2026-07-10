package com.example.practica_desarrollomovil.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sales",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("productId"), Index("soldAtMillis")]
)
data class SaleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val productId: Long?,
    val productName: String,
    val quantity: Double,
    val unitPrice: Double,
    val totalAmount: Double,
    val profitAmount: Double,
    val soldAtMillis: Long
)
