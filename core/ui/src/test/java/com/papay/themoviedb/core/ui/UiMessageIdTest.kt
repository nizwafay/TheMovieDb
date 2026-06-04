package com.papay.themoviedb.core.ui

import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UiMessageIdTest {
    @Test
    fun `next returns increasing ids`() {
        val firstId = UiMessageId.next()
        val secondId = UiMessageId.next()

        assertTrue(secondId > firstId)
    }

    @Test
    fun `ui messages receive unique ids by default`() {
        val firstMessage = UiMessage(
            titleRes = R.string.error_load_title,
            descriptionRes = R.string.error_load_description
        )
        val secondMessage = UiMessage(
            titleRes = R.string.error_load_title,
            descriptionRes = R.string.error_load_description
        )

        assertNotEquals(firstMessage.id, secondMessage.id)
    }
}
