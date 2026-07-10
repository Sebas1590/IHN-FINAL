package com.example.practica_desarrollomovil.data.repository

import com.example.practica_desarrollomovil.data.local.dao.ProductDao
import com.example.practica_desarrollomovil.data.mapper.ProductMapper
import com.example.practica_desarrollomovil.domain.model.Product
import com.example.practica_desarrollomovil.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(
    private val productDao: ProductDao
) : ProductRepository {

    override fun observeAllProducts(): Flow<List<Product>> =
        productDao.observeAll().map { list -> list.map(ProductMapper::toDomain) }

    override fun observeProduct(id: Long): Flow<Product?> =
        productDao.observeById(id).map { entity -> entity?.let(ProductMapper::toDomain) }

    override suspend fun getProduct(id: Long): Product? =
        productDao.getById(id)?.let(ProductMapper::toDomain)

    override suspend fun upsertProduct(product: Product): Long {
        val now = System.currentTimeMillis()
        val entity = ProductMapper.toEntity(
            product.copy(
                updatedAtMillis = now,
                createdAtMillis = if (product.id == 0L) now else product.createdAtMillis
            )
        )
        return if (entity.id == 0L) {
            productDao.insert(entity)
        } else {
            productDao.update(entity)
            entity.id
        }
    }

    override suspend fun deleteProduct(id: Long) {
        productDao.getById(id)?.let { productDao.delete(it) }
    }
}
