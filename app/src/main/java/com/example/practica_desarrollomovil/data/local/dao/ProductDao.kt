package com.example.practica_desarrollomovil.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.practica_desarrollomovil.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun observeAll(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun observeById(id: Long): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: Long): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ProductEntity): Long

    @Update
    suspend fun update(entity: ProductEntity)

    @Delete
    suspend fun delete(entity: ProductEntity)

    @Query("UPDATE products SET stock = :newStock, updatedAtMillis = :updatedAt WHERE id = :id")
    suspend fun updateStock(id: Long, newStock: Double, updatedAt: Long)
}
