package com.example.practica_desarrollomovil.domain.model

enum class ActivityType {
    SALE,
    STOCK
}

data class RecentActivity(
    val id: String,
    val type: ActivityType,
    val title: String,
    val subtitle: String,
    val detail: String,
    val timestampMillis: Long
)
