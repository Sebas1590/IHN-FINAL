package com.example.practica_desarrollomovil.domain.model

data class Product(
    val id: Long = 0L,
    val name: String,
    val stock: Double,
    val unit: ProductUnit,
    val pricePerUnit: Double,
    val totalInvestment: Double,
    val imageUri: String? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
) {
    val costPerUnit: Double
        get() = if (stock > 0) totalInvestment / stock else 0.0
}

enum class ProductUnit(val label: String, val allowsDecimals: Boolean) {
    UNID("Unid.", false),
    KG("kg", true),
    L("L", true),
    G("g", true),
    ML("ml", true);

    companion object {
        fun fromLabel(label: String): ProductUnit =
            entries.find { it.label == label } ?: UNID
    }
}
