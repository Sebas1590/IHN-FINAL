package com.example.practica_desarrollomovil.domain.model

/**
 * Reglas de negocio para avisar cuando el stock de un producto queda bajo o se agota.
 * Los umbrales dependen de la unidad para que "poco" tenga sentido en cada caso.
 */
object StockRules {

    /** A partir de este stock (o menos) se considera que "queda poco". */
    fun lowStockThreshold(unit: ProductUnit): Double = when (unit) {
        ProductUnit.UNID -> 5.0
        ProductUnit.KG, ProductUnit.L -> 2.0
        ProductUnit.G, ProductUnit.ML -> 250.0
    }

    fun isOut(stock: Double): Boolean = stock <= 0.0

    /** Queda poco pero todavía hay existencias. */
    fun isLow(stock: Double, unit: ProductUnit): Boolean =
        stock > 0.0 && stock <= lowStockThreshold(unit)

    /** True si conviene avisar al usuario (agotado o poco stock). */
    fun needsAlert(stock: Double, unit: ProductUnit): Boolean =
        isOut(stock) || isLow(stock, unit)
}

/** Producto cuyo stock quedó bajo o agotado después de una venta. */
data class LowStockItem(
    val productName: String,
    val remainingStock: Double,
    val unit: ProductUnit
) {
    val isOut: Boolean get() = StockRules.isOut(remainingStock)
}
