package com.papay.themoviedb.core.network

import com.papay.themoviedb.core.domain.error.AppException
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val accessToken: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val sanitizedAccessToken = accessToken.trim()

        if (sanitizedAccessToken.isBlank()) {
            throw AppException.MissingAccessToken()
        }

        if (sanitizedAccessToken.isLegacyApiKey()) {
            throw AppException.InvalidAccessTokenType()
        }

        val request = chain.request()
            .newBuilder()
            .header("Accept", "application/json")
            .header("Authorization", "Bearer $sanitizedAccessToken")
            .build()

        return chain.proceed(request)
    }

    private fun String.isLegacyApiKey(): Boolean {
        return length == LegacyApiKeyLength && all { character -> character.isDigit() || character in 'a'..'f' }
    }

    private companion object {
        const val LegacyApiKeyLength = 32
    }
}
