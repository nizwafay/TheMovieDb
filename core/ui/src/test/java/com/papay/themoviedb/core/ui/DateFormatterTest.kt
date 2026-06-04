package com.papay.themoviedb.core.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Locale

class DateFormatterTest {
    @Test
    fun `formatDate returns human readable date`() {
        val formattedDate = DateFormatter.formatDate(
            date = "2026-06-04",
            locale = Locale.US
        )

        assertEquals("June 4, 2026", formattedDate)
    }

    @Test
    fun `formatDate ignores time portion from ISO date time`() {
        val formattedDate = DateFormatter.formatDate(
            date = "2026-06-04T02:43:21.000Z",
            locale = Locale.US
        )

        assertEquals("June 4, 2026", formattedDate)
    }

    @Test
    fun `formatDate returns original value when date cannot be parsed`() {
        val formattedDate = DateFormatter.formatDate(
            date = "not-a-date",
            locale = Locale.US
        )

        assertEquals("not-a-date", formattedDate)
    }

    @Test
    fun `formatDate returns null when date is null`() {
        assertNull(DateFormatter.formatDate(date = null, locale = Locale.US))
    }

    @Test
    fun `formatDate returns null when date is blank`() {
        assertNull(DateFormatter.formatDate(date = " ", locale = Locale.US))
    }
}
