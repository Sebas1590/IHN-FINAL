package com.example.practica_desarrollomovil.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import androidx.room.Relation
import androidx.room.Embedded
import com.example.practica_desarrollomovil.data.local.entity.SaleEntity
import com.example.practica_desarrollomovil.data.local.entity.ReceiptEntity
import com.example.practica_desarrollomovil.data.local.entity.ReceiptItemEntity
import kotlinx.coroutines.flow.Flow

data class ReceiptWithItems(
    @Embedded val receipt: ReceiptEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "receiptId"
    )
    val items: List<ReceiptItemEntity>
)

@Dao
interface SaleDao {
    @Transaction
    @Query("SELECT * FROM receipts ORDER BY soldAtMillis DESC")
    fun observeAllReceipts(): Flow<List<ReceiptWithItems>>

    @Transaction
    @Query("SELECT * FROM receipts WHERE id = :id")
    suspend fun getReceiptWithItemsById(id: Long): ReceiptWithItems?

    @Query(
        """
        SELECT COUNT(*) as salesCount, 
               TOTAL(totalAmount) as income, 
               TOTAL(totalProfit) as profit 
        FROM receipts 
        WHERE soldAtMillis >= :startOfDay AND soldAtMillis <= :endOfDay
        """
    )
    fun observeDailyTotals(startOfDay: Long, endOfDay: Long): Flow<DailyTotalsRow>

    @Query(
        """
        SELECT COALESCE(SUM(totalProfit), 0) FROM receipts 
        WHERE soldAtMillis >= :startMillis AND soldAtMillis <= :endMillis
        """
    )
    fun observeProfitBetween(startMillis: Long, endMillis: Long): Flow<Double>

    @Query(
        """
        SELECT COUNT(*) FROM receipts 
        WHERE soldAtMillis >= :startMillis AND soldAtMillis <= :endMillis
        """
    )
    fun observeSalesCountBetween(startMillis: Long, endMillis: Long): Flow<Int>

    @Transaction
    @Query(
        """
        SELECT * FROM receipts 
        ORDER BY soldAtMillis DESC 
        LIMIT :limit
        """
    )
    fun observeRecentReceipts(limit: Int): Flow<List<ReceiptWithItems>>

    @Query("SELECT EXISTS(SELECT 1 FROM receipt_items WHERE productId = :productId)")
    suspend fun hasSalesForProduct(productId: Long): Boolean

    @Insert
    suspend fun insertReceipt(receipt: ReceiptEntity): Long

    @Insert
    suspend fun insertReceiptItems(items: List<ReceiptItemEntity>)

    @Update
    suspend fun updateReceipt(receipt: ReceiptEntity)

    @Query("DELETE FROM receipt_items WHERE receiptId = :receiptId")
    suspend fun deleteReceiptItems(receiptId: Long)

    @Query("DELETE FROM receipts WHERE id = :id")
    suspend fun deleteReceiptById(id: Long)

    // Legacy support or temporary for migration
    @Query("SELECT * FROM sales ORDER BY soldAtMillis DESC")
    fun observeAll(): Flow<List<SaleEntity>>

    @Query(
        """
        SELECT * FROM sales 
        ORDER BY soldAtMillis DESC 
        LIMIT :limit
        """
    )
    fun observeRecent(limit: Int): Flow<List<SaleEntity>>

    @Insert
    suspend fun insert(sale: SaleEntity): Long

    @Update
    suspend fun update(sale: SaleEntity)

    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getById(id: Long): SaleEntity?
}

data class DailyTotalsRow(
    val salesCount: Int,
    val income: Double?,
    val profit: Double?
)
