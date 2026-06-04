package com.papay.themoviedb.core.ui

import com.papay.themoviedb.core.domain.error.AppException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class UiMessageMapperTest {
    @Test
    fun `emptyUiMessage returns empty state message`() {
        val message = emptyUiMessage()

        assertEquals(R.string.state_empty_title, message.titleRes)
        assertEquals(R.string.state_empty_description, message.descriptionRes)
        assertTrue(message.canRetry)
    }

    @Test
    fun `emptyUiMessageIf returns empty message when list is empty`() {
        val message = emptyUiMessageIf(emptyList<String>())

        assertEquals(R.string.state_empty_title, message?.titleRes)
        assertEquals(R.string.state_empty_description, message?.descriptionRes)
    }

    @Test
    fun `emptyUiMessageIf returns null when list has items`() {
        assertNull(emptyUiMessageIf(listOf("item")))
    }

    @Test
    fun `toUiMessage maps auth setup errors to connection message without retry`() {
        val errors = listOf(
            AppException.MissingAccessToken(),
            AppException.InvalidAccessTokenType(),
            AppException.Unauthorized()
        )

        errors.forEach { error ->
            val message = error.toUiMessage(hasCachedData = false)

            assertEquals(R.string.error_connection_title, message.titleRes)
            assertEquals(R.string.error_connection_description, message.descriptionRes)
            assertFalse(message.canRetry)
        }
    }

    @Test
    fun `toUiMessage maps rate limited error without cached data`() {
        val message = AppException.RateLimited().toUiMessage(hasCachedData = false)

        assertEquals(R.string.error_rate_limited_title, message.titleRes)
        assertEquals(R.string.error_rate_limited_description, message.descriptionRes)
        assertTrue(message.canRetry)
    }

    @Test
    fun `toUiMessage maps server error without cached data`() {
        val message = AppException.ServerError().toUiMessage(hasCachedData = false)

        assertEquals(R.string.error_load_title, message.titleRes)
        assertEquals(R.string.error_load_description, message.descriptionRes)
    }

    @Test
    fun `toUiMessage maps offline error without cached data`() {
        val message = AppException.NetworkUnavailable(IOException()).toUiMessage(hasCachedData = false)

        assertEquals(R.string.error_offline_title, message.titleRes)
        assertEquals(R.string.error_offline_description, message.descriptionRes)
    }

    @Test
    fun `toUiMessage keeps error title and uses cached description when cached data exists`() {
        val errors = listOf(
            AppException.RateLimited() to R.string.error_rate_limited_title,
            AppException.ServerError() to R.string.error_load_title,
            AppException.NetworkUnavailable(IOException()) to R.string.error_offline_title,
            RuntimeException() to R.string.error_load_title
        )

        errors.forEach { (error, expectedTitleRes) ->
            val message = error.toUiMessage(hasCachedData = true)

            assertEquals(expectedTitleRes, message.titleRes)
            assertEquals(R.string.state_showing_cached_description, message.descriptionRes)
        }
    }

    @Test
    fun `toUiMessage maps unexpected error to load message`() {
        val message = RuntimeException().toUiMessage(hasCachedData = false)

        assertEquals(R.string.error_load_title, message.titleRes)
        assertEquals(R.string.error_load_description, message.descriptionRes)
    }
}
