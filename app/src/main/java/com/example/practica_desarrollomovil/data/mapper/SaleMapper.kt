package com.example.practica_desarrollomovil.data.mapper

import com.example.practica_desarrollomovil.data.local.entity.SaleEntity
import com.example.practica_desarrollomovil.domain.model.Sale

object SaleMapper {
    fun toDomain(entity: SaleEntity): Sale = Sale(
        id = entity.id,
        productId = entity.productId,
        productName = entity.productName,
        quantity = entity.quantity,
        unitPrice = entity.unitPrice,
        totalAmount = entity.totalAmount,
        profitAmount = entity.profitAmount,
        soldAtMillis = entity.soldAtMillis
    )

    fun toEntity(domain: Sale): SaleEntity = SaleEntity(
        id = domain.id,
        productId = domain.productId,
        productName = domain.productName,
        quantity = domain.quantity,
        unitPrice = domain.unitPrice,
        totalAmount = domain.totalAmount,
        profitAmount = domain.profitAmount,
        soldAtMillis = domain.soldAtMillis
    )
}
