package com.papay.themoviedb.core.ui

import com.papay.themoviedb.core.domain.error.AppException

fun emptyUiMessage(): UiMessage = UiMessage(
    titleRes = R.string.state_empty_title,
    descriptionRes = R.string.state_empty_description
)

fun <T> emptyUiMessageIf(items: List<T>): UiMessage? {
    return if (items.isEmpty()) emptyUiMessage() else null
}

fun Throwable.toUiMessage(hasCachedData: Boolean): UiMessage {
    return when (this) {
        is AppException.MissingAccessToken,
        is AppException.Unauthorized,
        is AppException.InvalidAccessTokenType -> UiMessage(
            titleRes = R.string.error_connection_title,
            descriptionRes = R.string.error_connection_description,
            canRetry = false
        )

        is AppException.RateLimited -> cacheAwareMessage(
            hasCachedData = hasCachedData,
            titleRes = R.string.error_rate_limited_title,
            descriptionRes = R.string.error_rate_limited_description
        )

        is AppException.ServerError -> cacheAwareMessage(
            hasCachedData = hasCachedData,
            titleRes = R.string.error_load_title,
            descriptionRes = R.string.error_load_description
        )

        is AppException.NetworkUnavailable -> cacheAwareMessage(
            hasCachedData = hasCachedData,
            titleRes = R.string.error_offline_title,
            descriptionRes = R.string.error_offline_description
        )

        else -> cacheAwareMessage(
            hasCachedData = hasCachedData,
            titleRes = R.string.error_load_title,
            descriptionRes = R.string.error_load_description
        )
    }
}

private fun cacheAwareMessage(
    hasCachedData: Boolean,
    titleRes: Int,
    descriptionRes: Int
): UiMessage = UiMessage(
    titleRes = titleRes,
    descriptionRes = if (hasCachedData) R.string.state_showing_cached_description else descriptionRes
)
