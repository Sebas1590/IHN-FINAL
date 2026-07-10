package com.example.practica_desarrollomovil.util

import com.example.practica_desarrollomovil.domain.model.ProductUnit
import java.util.Locale

/**
 * Formatea cantidades de stock/venta respetando la unidad del producto.
 *
 * - Unidades enteras (p. ej. "Unid.") nunca muestran decimales.
 * - El resto muestra hasta 2 decimales, eliminando ceros sobrantes ("2.50" -> "2.5").
 * - Los valores enteros siempre se muestran sin ".0" ("5.0" -> "5").
 */
object QuantityFormatter {

    /** Formatea usando la unidad para decidir si se permiten decimales. */
    fun format(quantity: Double, unit: ProductUnit): String {
        if (!unit.allowsDecimals) {
            return quantity.toLong().toString()
        }
        return format(quantity)
    }

    /**
     * Formatea sin conocer la unidad: si el valor es entero se muestra sin decimales,
     * de lo contrario con hasta 2 decimales sin ceros sobrantes.
     */
    fun format(quantity: Double): String {
        return if (quantity % 1.0 == 0.0) {
            quantity.toLong().toString()
        } else {
            String.format(Locale.US, "%.2f", quantity)
                .trimEnd('0')
                .trimEnd('.')
        }
    }

    /** Texto listo para un campo de edición (mismo criterio que [format]). */
    fun toInput(quantity: Double, unit: ProductUnit): String = format(quantity, unit)

    /** Cantidad + etiqueta de unidad, p. ej. "5 Unid." o "2.5 kg". */
    fun withUnit(quantity: Double, unit: ProductUnit): String =
        "${format(quantity, unit)} ${unit.label}"
}
