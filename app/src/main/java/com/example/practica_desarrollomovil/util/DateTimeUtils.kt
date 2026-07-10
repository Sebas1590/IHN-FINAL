package com.example.practica_desarrollomovil.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {
    private val locale = Locale("es", "PE")

    fun startOfDayMillis(calendar: Calendar = Calendar.getInstance()): Long {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun endOfDayMillis(calendar: Calendar = Calendar.getInstance()): Long {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.add(Calendar.DAY_OF_YEAR, 1)
        return cal.timeInMillis
    }

    fun startOfWeekMillis(): Long {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return startOfDayMillis(cal)
    }

    fun startOfMonthMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        return startOfDayMillis(cal)
    }

    fun formatHeaderDate(millis: Long = System.currentTimeMillis()): String {
        val format = SimpleDateFormat("EEEE, d 'de' MMMM", locale)
        val raw = format.format(Date(millis))
        return raw.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    }

    fun formatTime(millis: Long): String {
        val format = SimpleDateFormat("hh:mm a", locale)
        return format.format(Date(millis))
    }

    fun formatDateShort(millis: Long): String {
        val format = SimpleDateFormat("dd/MM/yyyy", locale)
        return format.format(Date(millis))
    }

    fun parseDateShort(value: String): Long? = runCatching {
        val format = SimpleDateFormat("dd/MM/yyyy", locale)
        format.isLenient = false
        format.parse(value)?.time
    }.getOrNull()

    fun todayDateShort(): String = formatDateShort(System.currentTimeMillis())

    fun formatMonthName(millis: Long): String {
        val format = SimpleDateFormat("MMMM", locale)
        return format.format(Date(millis)).replaceFirstChar { it.uppercase() }
    }

    fun formatDateRange(start: Long, end: Long): String {
        val format = SimpleDateFormat("dd/MM", locale)
        return "Desde ${format.format(Date(start))} al ${format.format(Date(end))}"
    }

    fun startOfDayMillisFor(dateMillis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = dateMillis
        return startOfDayMillis(cal)
    }

    fun endOfDayMillisFor(dateMillis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = dateMillis
        return endOfDayMillis(cal)
    }
}
