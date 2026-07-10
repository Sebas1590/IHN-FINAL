package com.example.practica_desarrollomovil.data.mapper

import com.example.practica_desarrollomovil.data.local.dao.ReceiptWithItems
import com.example.practica_desarrollomovil.data.local.entity.ReceiptEntity
import com.example.practica_desarrollomovil.data.local.entity.ReceiptItemEntity
import com.example.practica_desarrollomovil.domain.model.Receipt
import com.example.practica_desarrollomovil.domain.model.ReceiptItem

object ReceiptMapper {
    fun toDomain(relation: ReceiptWithItems): Receipt = Receipt(
        id = relation.receipt.id,
        totalAmount = relation.receipt.totalAmount,
        totalProfit = relation.receipt.totalProfit,
        soldAtMillis = relation.receipt.soldAtMillis,
        items = relation.items.map { toDomain(it) }
    )

    fun toDomain(entity: ReceiptItemEntity): ReceiptItem = ReceiptItem(
        id = entity.id,
        productId = entity.productId,
        productName = entity.productName,
        quantity = entity.quantity,
        unitPrice = entity.unitPrice,
        totalAmount = entity.totalAmount,
        profitAmount = entity.profitAmount
    )

    fun toEntity(domain: Receipt): ReceiptEntity = ReceiptEntity(
        id = domain.id,
        totalAmount = domain.totalAmount,
        totalProfit = domain.totalProfit,
        soldAtMillis = domain.soldAtMillis
    )

    fun toEntity(domain: ReceiptItem, receiptId: Long): ReceiptItemEntity = ReceiptItemEntity(
        id = domain.id,
        receiptId = receiptId,
        productId = domain.productId,
        productName = domain.productName,
        quantity = domain.quantity,
        unitPrice = domain.unitPrice,
        totalAmount = domain.totalAmount,
        profitAmount = domain.profitAmount
    )
}
