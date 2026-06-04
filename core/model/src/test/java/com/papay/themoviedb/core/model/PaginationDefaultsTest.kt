package com.papay.themoviedb.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class PaginationDefaultsTest {
    @Test
    fun `first page is one`() {
        assertEquals(1, PaginationDefaults.FirstPage)
    }
}
