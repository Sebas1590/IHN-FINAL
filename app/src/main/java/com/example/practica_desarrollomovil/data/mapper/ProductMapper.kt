package com.example.practica_desarrollomovil.data.mapper

import com.example.practica_desarrollomovil.data.local.entity.ProductEntity
import com.example.practica_desarrollomovil.domain.model.Product
import com.example.practica_desarrollomovil.domain.model.ProductUnit

object ProductMapper {
    fun toDomain(entity: ProductEntity): Product = Product(
        id = entity.id,
        name = entity.name,
        stock = entity.stock,
        unit = ProductUnit.fromLabel(entity.unit),
        pricePerUnit = entity.pricePerUnit,
        totalInvestment = entity.totalInvestment,
        imageUri = entity.imageUri,
        createdAtMillis = entity.createdAtMillis,
        updatedAtMillis = entity.updatedAtMillis
    )

    fun toEntity(domain: Product): ProductEntity = ProductEntity(
        id = domain.id,
        name = domain.name.trim(),
        stock = domain.stock,
        unit = domain.unit.label,
        pricePerUnit = domain.pricePerUnit,
        totalInvestment = domain.totalInvestment,
        imageUri = domain.imageUri,
        createdAtMillis = domain.createdAtMillis,
        updatedAtMillis = domain.updatedAtMillis
    )
}
