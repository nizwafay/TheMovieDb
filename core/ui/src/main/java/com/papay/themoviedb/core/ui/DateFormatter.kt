package com.papay.themoviedb.core.ui

import java.text.SimpleDateFormat
import java.util.Locale

object DateFormatter {
    fun formatDate(date: String?, locale: Locale = Locale.getDefault()): String? {
        if (date.isNullOrBlank()) return null

        return runCatching {
            val normalizedDate = date.substringBefore(DateTimeSeparator)
            val parser = SimpleDateFormat(IsoDatePattern, Locale.US)
            val formatter = SimpleDateFormat(HumanReadableDatePattern, locale)
            formatter.format(requireNotNull(parser.parse(normalizedDate)))
        }.getOrElse {
            date
        }
    }
}

private const val DateTimeSeparator = "T"
private const val IsoDatePattern = "yyyy-MM-dd"
private const val HumanReadableDatePattern = "MMMM d, yyyy"
