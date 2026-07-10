package com.example.practica_desarrollomovil.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.practica_desarrollomovil.data.local.dao.ProductDao
import com.example.practica_desarrollomovil.data.local.dao.SaleDao
import com.example.practica_desarrollomovil.data.local.entity.ProductEntity
import com.example.practica_desarrollomovil.data.local.entity.SaleEntity
import com.example.practica_desarrollomovil.data.local.entity.ReceiptEntity
import com.example.practica_desarrollomovil.data.local.entity.ReceiptItemEntity

@Database(
    entities = [ProductEntity::class, SaleEntity::class, ReceiptEntity::class, ReceiptItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MetamercaDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao
}
