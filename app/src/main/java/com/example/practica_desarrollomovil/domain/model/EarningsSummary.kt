package com.example.practica_desarrollomovil.domain.model

data class EarningsSummary(
    val profitToday: Double = 0.0,
    val profitThisWeek: Double = 0.0,
    val profitThisMonth: Double = 0.0,
    val salesCountToday: Int = 0,
    val salesCountThisWeek: Int = 0,
    val salesCountThisMonth: Int = 0
)
