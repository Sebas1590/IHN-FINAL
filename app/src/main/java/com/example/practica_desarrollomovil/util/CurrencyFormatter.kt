package com.example.practica_desarrollomovil.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    private val format = NumberFormat.getNumberInstance(Locale("es", "PE")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    fun formatSoles(amount: Double, withSign: Boolean = false): String {
        val prefix = when {
            withSign && amount > 0 -> "+"
            withSign && amount < 0 -> "-"
            else -> ""
        }
        val value = kotlin.math.abs(amount)
        return "${prefix}S/ ${format.format(value)}"
    }

    fun formatSolesCompact(amount: Double): String {
        return if (amount % 1.0 == 0.0) {
            "S/ ${amount.toInt()}"
        } else {
            formatSoles(amount)
        }
    }
}
