package com.example.practica_desarrollomovil.presentation.navigation

object Routes {
    const val LOGIN = "login"
    const val MAIN = "main"
    const val HOME = "home"
    const val PRODUCTS = "products"
    const val PRODUCT_ADD = "product_add"
    const val PRODUCT_EDIT = "product_edit/{productId}"
    const val SALES = "sales"
    const val REGISTER_SALE = "register_sale"
    const val RECEIPT_EDIT = "receipt_edit/{receiptId}"
    const val EARNINGS = "earnings"
    const val ACCESSIBILITY = "accessibility"

    fun productEdit(productId: Long) = "product_edit/$productId"
    fun receiptEdit(receiptId: Long) = "receipt_edit/$receiptId"
}
