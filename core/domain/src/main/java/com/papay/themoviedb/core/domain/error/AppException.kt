package com.papay.themoviedb.core.domain.error

sealed class AppException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) {
    class MissingAccessToken : AppException(
        message = "TMDB access token is missing."
    )

    class InvalidAccessTokenType : AppException(
        message = "TMDB access token is not a read access token."
    )

    class Unauthorized : AppException(
        message = "TMDB rejected the access token."
    )

    class RateLimited : AppException(
        message = "TMDB rate limit reached."
    )

    class ServerError : AppException(
        message = "TMDB returned a server error."
    )

    class NetworkUnavailable(cause: Throwable) : AppException(
        message = "Network is unavailable.",
        cause = cause
    )

    class Unexpected(cause: Throwable) : AppException(
        message = "Unexpected error.",
        cause = cause
    )
}
