package com.alhadi.cmms.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateStrings {
    private fun formatter() = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        isLenient = false
    }

    private fun dateTimeFormatter() = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

    fun today(): String = formatter().format(Date())

    fun now(): String = dateTimeFormatter().format(Date())

    fun daysFromToday(days: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, days)
        return formatter().format(calendar.time)
    }

    fun addDays(date: String, days: Int): String {
        val calendar = Calendar.getInstance()
        calendar.time = parseOrToday(date)
        calendar.add(Calendar.DATE, days)
        return formatter().format(calendar.time)
    }

    fun isDueOrOverdue(date: String, today: String = today()): Boolean = date <= today

    /** Absolute number of days between two yyyy-MM-dd dates. */
    fun daysBetween(a: String, b: String): Long {
        val ms = kotlin.math.abs(parseOrToday(a).time - parseOrToday(b).time)
        return ms / (1000L * 60 * 60 * 24)
    }

    private fun parseOrToday(value: String): Date {
        return try {
            formatter().parse(value) ?: Date()
        } catch (_: ParseException) {
            Date()
        }
    }
}
