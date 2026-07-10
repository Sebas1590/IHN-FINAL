package com.example.practica_desarrollomovil.data.repository

import com.example.practica_desarrollomovil.data.local.dao.ProductDao
import com.example.practica_desarrollomovil.data.local.dao.SaleDao
import com.example.practica_desarrollomovil.data.local.entity.SaleEntity
import com.example.practica_desarrollomovil.data.local.entity.ReceiptEntity
import com.example.practica_desarrollomovil.data.local.entity.ReceiptItemEntity
import com.example.practica_desarrollomovil.data.mapper.SaleMapper
import com.example.practica_desarrollomovil.data.mapper.ReceiptMapper
import com.example.practica_desarrollomovil.domain.model.ActivityType
import com.example.practica_desarrollomovil.domain.model.DashboardSummary
import com.example.practica_desarrollomovil.domain.model.EarningsSummary
import com.example.practica_desarrollomovil.domain.model.RecentActivity
import com.example.practica_desarrollomovil.domain.model.Sale
import com.example.practica_desarrollomovil.domain.model.Receipt
import com.example.practica_desarrollomovil.domain.model.ReceiptItem
import com.example.practica_desarrollomovil.domain.repository.SaleRepository
import com.example.practica_desarrollomovil.util.CurrencyFormatter
import com.example.practica_desarrollomovil.util.DateTimeUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class SaleRepositoryImpl(
    private val saleDao: SaleDao,
    private val productDao: ProductDao
) : SaleRepository {

    override fun observeDashboardSummary(): Flow<DashboardSummary> =
        saleDao.observeDailyTotals(
            DateTimeUtils.startOfDayMillis(),
            DateTimeUtils.endOfDayMillis()
        ).map { row ->
            DashboardSummary(
                salesCountToday = row.salesCount,
                incomeToday = row.income ?: 0.0,
                netProfitToday = row.profit ?: 0.0
            )
        }

    override fun observeEarningsSummary(): Flow<EarningsSummary> {
        val weekStart = DateTimeUtils.startOfWeekMillis()
        val monthStart = DateTimeUtils.startOfMonthMillis()
        val dayStart = DateTimeUtils.startOfDayMillis()
        val now = System.currentTimeMillis()

        return combine(
            saleDao.observeProfitBetween(dayStart, now),
            saleDao.observeProfitBetween(weekStart, now),
            saleDao.observeProfitBetween(monthStart, now),
            saleDao.observeSalesCountBetween(dayStart, now),
            saleDao.observeSalesCountBetween(weekStart, now),
            saleDao.observeSalesCountBetween(monthStart, now)
        ) { results: Array<Any> ->
            EarningsSummary(
                profitToday = results[0] as Double,
                profitThisWeek = results[1] as Double,
                profitThisMonth = results[2] as Double,
                salesCountToday = results[3] as Int,
                salesCountThisWeek = results[4] as Int,
                salesCountThisMonth = results[5] as Int
            )
        }
    }

    override fun observeRecentActivity(limit: Int): Flow<List<RecentActivity>> =
        combine(
            saleDao.observeRecentReceipts(limit),
            productDao.observeAll()
        ) { receipts, products ->
            val receiptActivities = receipts.map { receipt ->
                RecentActivity(
                    id = "receipt_${receipt.receipt.id}",
                    type = ActivityType.SALE,
                    title = "Venta #${receipt.receipt.id}",
                    subtitle = DateTimeUtils.formatTime(receipt.receipt.soldAtMillis),
                    detail = CurrencyFormatter.formatSoles(receipt.receipt.totalAmount, withSign = true),
                    timestampMillis = receipt.receipt.soldAtMillis
                )
            }
            // Filter out stock updates that happened exactly at the same time as a receipt (linked updates)
            // to avoid showing separate "Stock: Product" lines for items just sold.
            val saleTimestamps = receipts.map { it.receipt.soldAtMillis }.toSet()

            val stockActivities = products
                .filter { it.updatedAtMillis !in saleTimestamps } // Grouping logic: hide if it's a sale
                .sortedByDescending { it.updatedAtMillis }
                .take(limit)
                .map { product ->
                    RecentActivity(
                        id = "stock_${product.id}",
                        type = ActivityType.STOCK,
                        title = "Stock: ${product.name}",
                        subtitle = DateTimeUtils.formatTime(product.updatedAtMillis),
                        detail = "+${product.stock.toInt()} ${product.unit}",
                        timestampMillis = product.updatedAtMillis
                    )
                }
            (receiptActivities + stockActivities)
                .sortedByDescending { it.timestampMillis }
                .take(limit)
        }

    override fun observeAllReceipts(): Flow<List<Receipt>> =
        saleDao.observeAllReceipts().map { list -> list.map(ReceiptMapper::toDomain) }

    override suspend fun getReceipt(id: Long): Receipt? =
        saleDao.getReceiptWithItemsById(id)?.let(ReceiptMapper::toDomain)

    override suspend fun registerReceipt(items: List<ReceiptItem>): Result<Receipt> {
        val now = System.currentTimeMillis()
        var totalAmount = 0.0
        var totalProfit = 0.0
        
        val entities = mutableListOf<ReceiptItemEntity>()
        
        for (item in items) {
            val productId = item.productId ?: continue
            val product = productDao.getById(productId) ?: continue
            
            if (item.quantity > product.stock) {
                return Result.failure(IllegalArgumentException("Stock insuficiente para ${product.name}"))
            }

            val costPerUnit = if (product.stock > 0) product.totalInvestment / product.stock else 0.0
            val profit = (product.pricePerUnit - costPerUnit) * item.quantity
            
            totalAmount += item.totalAmount
            totalProfit += profit
            
            entities.add(ReceiptItemEntity(
                receiptId = 0, // Placeholder
                productId = productId,
                productName = product.name,
                quantity = item.quantity,
                unitPrice = product.pricePerUnit,
                totalAmount = item.totalAmount,
                profitAmount = profit
            ))
            
            // Update stock
            productDao.updateStock(productId, product.stock - item.quantity, now)
        }

        val receiptId = saleDao.insertReceipt(ReceiptEntity(
            totalAmount = totalAmount,
            totalProfit = totalProfit,
            soldAtMillis = now
        ))

        saleDao.insertReceiptItems(entities.map { it.copy(receiptId = receiptId) })

        return Result.success(getReceipt(receiptId)!!)
    }

    override suspend fun updateReceipt(receiptId: Long, items: List<ReceiptItem>): Result<Receipt> {
        val oldReceipt = saleDao.getReceiptWithItemsById(receiptId)
            ?: return Result.failure(IllegalArgumentException("Venta no encontrada"))
        
        val now = System.currentTimeMillis()
        
        // Restore old stocks
        for (oldItem in oldReceipt.items) {
            oldItem.productId?.let { pid ->
                val p = productDao.getById(pid)
                if (p != null) {
                    productDao.updateStock(pid, p.stock + oldItem.quantity, now)
                }
            }
        }
        
        // Register as new (simplified for this update)
        saleDao.deleteReceiptItems(receiptId)
        
        var totalAmount = 0.0
        var totalProfit = 0.0
        val entities = mutableListOf<ReceiptItemEntity>()

        for (item in items) {
            val productId = item.productId ?: continue
            val product = productDao.getById(productId) ?: continue
            
            if (item.quantity > product.stock) {
                // Rollback if possible? Simplified: just return error. 
                // Better would be to transactionalize this.
                return Result.failure(IllegalArgumentException("Stock insuficiente para ${product.name}"))
            }

            val costPerUnit = if (product.stock > 0) product.totalInvestment / product.stock else 0.0
            val profit = (product.pricePerUnit - costPerUnit) * item.quantity
            
            totalAmount += item.totalAmount
            totalProfit += profit
            
            entities.add(ReceiptItemEntity(
                receiptId = receiptId,
                productId = productId,
                productName = product.name,
                quantity = item.quantity,
                unitPrice = product.pricePerUnit,
                totalAmount = item.totalAmount,
                profitAmount = profit
            ))
            
            productDao.updateStock(productId, product.stock - item.quantity, now)
        }
        
        saleDao.updateReceipt(oldReceipt.receipt.copy(
            totalAmount = totalAmount,
            totalProfit = totalProfit,
            soldAtMillis = now
        ))
        
        saleDao.insertReceiptItems(entities)
        
        return Result.success(getReceipt(receiptId)!!)
    }

    override suspend fun deleteReceipt(id: Long): Result<Unit> {
        val receipt = saleDao.getReceiptWithItemsById(id) ?: return Result.success(Unit)
        val now = System.currentTimeMillis()
        
        for (item in receipt.items) {
            item.productId?.let { pid ->
                val p = productDao.getById(pid)
                if (p != null) {
                    productDao.updateStock(pid, p.stock + item.quantity, now)
                }
            }
        }
        
        saleDao.deleteReceiptById(id)
        return Result.success(Unit)
    }

    // LEGACY IMPLEMENTATIONS - Now reporting from grouped receipts
    override fun observeAllSales(): Flow<List<Sale>> =
        saleDao.observeAllReceipts().map { list -> 
            list.flatMap { receipt -> 
                receipt.items.map { item ->
                    Sale(
                        id = item.id,
                        productId = item.productId ?: 0L,
                        productName = item.productName,
                        quantity = item.quantity,
                        unitPrice = item.unitPrice,
                        totalAmount = item.totalAmount,
                        profitAmount = item.profitAmount,
                        soldAtMillis = receipt.receipt.soldAtMillis
                    )
                }
            }
        }

    override suspend fun hasSalesForProduct(productId: Long): Boolean =
        saleDao.hasSalesForProduct(productId)

    override suspend fun getSale(id: Long): Sale? =
        saleDao.getById(id)?.let(SaleMapper::toDomain)

    override suspend fun registerSale(productId: Long, quantity: Double): Result<Sale> {
        val product = productDao.getById(productId)
            ?: return Result.failure(IllegalArgumentException("Producto no encontrado"))

        if (quantity <= 0) {
            return Result.failure(IllegalArgumentException("Cantidad inválida"))
        }
        if (quantity > product.stock) {
            return Result.failure(IllegalArgumentException("Stock insuficiente"))
        }

        val costPerUnit = if (product.stock > 0) product.totalInvestment / product.stock else 0.0
        val unitPrice = product.pricePerUnit
        val totalAmount = unitPrice * quantity
        val profitAmount = (unitPrice - costPerUnit) * quantity
        val now = System.currentTimeMillis()

        val saleEntity = SaleEntity(
            productId = product.id,
            productName = product.name,
            quantity = quantity,
            unitPrice = unitPrice,
            totalAmount = totalAmount,
            profitAmount = profitAmount,
            soldAtMillis = now
        )
        val saleId = saleDao.insert(saleEntity)

        val newStock = product.stock - quantity
        productDao.updateStock(product.id, newStock, now)

        val sale = Sale(
            id = saleId,
            productId = product.id,
            productName = product.name,
            quantity = quantity,
            unitPrice = unitPrice,
            totalAmount = totalAmount,
            profitAmount = profitAmount,
            soldAtMillis = now
        )
        return Result.success(sale)
    }

    override suspend fun updateSale(saleId: Long, newQuantity: Double): Result<Sale> {
        val oldSaleEntity = saleDao.getById(saleId)
            ?: return Result.failure(IllegalArgumentException("Venta no encontrada"))
        
        val productId = oldSaleEntity.productId 
            ?: return Result.failure(IllegalArgumentException("No se puede editar ventas de productos eliminados"))
            
        val product = productDao.getById(productId)
            ?: return Result.failure(IllegalArgumentException("Producto no encontrado"))

        if (newQuantity <= 0) {
            return Result.failure(IllegalArgumentException("Cantidad inválida"))
        }

        val availableStock = product.stock + oldSaleEntity.quantity
        if (newQuantity > availableStock) {
            return Result.failure(IllegalArgumentException("Stock insuficiente"))
        }

        val costPerUnit = if (product.stock + oldSaleEntity.quantity > 0) 
            product.totalInvestment / (product.stock + oldSaleEntity.quantity) 
            else 0.0
            
        val unitPrice = product.pricePerUnit
        val totalAmount = unitPrice * newQuantity
        val profitAmount = (unitPrice - costPerUnit) * newQuantity
        val now = System.currentTimeMillis()

        val updatedSaleEntity = oldSaleEntity.copy(
            quantity = newQuantity,
            unitPrice = unitPrice,
            totalAmount = totalAmount,
            profitAmount = profitAmount,
            soldAtMillis = now
        )
        
        saleDao.update(updatedSaleEntity)

        val newStock = availableStock - newQuantity
        productDao.updateStock(productId, newStock, now)

        return Result.success(SaleMapper.toDomain(updatedSaleEntity))
    }
}
