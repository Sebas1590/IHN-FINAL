package com.example.practica_desarrollomovil.domain.repository

import com.example.practica_desarrollomovil.domain.model.DashboardSummary
import com.example.practica_desarrollomovil.domain.model.EarningsSummary
import com.example.practica_desarrollomovil.domain.model.RecentActivity
import com.example.practica_desarrollomovil.domain.model.Sale
import com.example.practica_desarrollomovil.domain.model.Receipt
import com.example.practica_desarrollomovil.domain.model.ReceiptItem
import kotlinx.coroutines.flow.Flow

interface SaleRepository {
    fun observeDashboardSummary(): Flow<DashboardSummary>
    fun observeEarningsSummary(): Flow<EarningsSummary>
    fun observeRecentActivity(limit: Int = 10): Flow<List<RecentActivity>>
    
    // New Receipt based methods
    fun observeAllReceipts(): Flow<List<Receipt>>
    suspend fun getReceipt(id: Long): Receipt?
    suspend fun registerReceipt(items: List<ReceiptItem>): Result<Receipt>
    suspend fun updateReceipt(receiptId: Long, items: List<ReceiptItem>): Result<Receipt>
    suspend fun deleteReceipt(id: Long): Result<Unit>

    // Legacy support
    fun observeAllSales(): Flow<List<Sale>>
    suspend fun hasSalesForProduct(productId: Long): Boolean
    suspend fun getSale(id: Long): Sale?
    suspend fun registerSale(productId: Long, quantity: Double): Result<Sale>
    suspend fun updateSale(saleId: Long, newQuantity: Double): Result<Sale>
}
