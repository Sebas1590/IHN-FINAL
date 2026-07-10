package com.example.practica_desarrollomovil.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val stock: Double,
    val unit: String,
    val pricePerUnit: Double,
    val totalInvestment: Double,
    val imageUri: String?,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)
