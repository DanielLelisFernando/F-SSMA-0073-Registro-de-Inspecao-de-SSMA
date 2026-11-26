package br.com.ssma.inspecoes

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private fun dateFormatter(): SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).apply {
    timeZone = TimeZone.getTimeZone("UTC")
    isLenient = false
}

fun dateTextToPickerMillis(raw: String): Long? {
    if (!raw.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))) return null
    val position = ParsePosition(0)
    val parsed = dateFormatter().parse(raw, position) ?: return null
    if (position.index != raw.length) return null
    return parsed.time
}

fun datePickerMillisToText(ms: Long): String = dateFormatter().format(Date(ms))

fun dateTextToSortableKey(raw: String): Long? {
    if (dateTextToPickerMillis(raw) == null) return null
    val parts = raw.split("/")
    return parts[2].toLong() * 10000L + parts[1].toLong() * 100L + parts[0].toLong()
}

fun dateKey(raw: String): Long = dateTextToSortableKey(raw) ?: 0L
