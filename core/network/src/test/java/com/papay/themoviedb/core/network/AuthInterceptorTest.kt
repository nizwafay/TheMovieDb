package com.papay.themoviedb.core.network

import com.papay.themoviedb.core.domain.error.AppException
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class AuthInterceptorTest {
    @Test
    fun `intercept adds accept and bearer authorization headers`() {
        val chain = mockChainWithCapturedRequest()

        AuthInterceptor(accessToken = AccessToken).intercept(chain.value)

        val request = chain.proceededRequest.captured
        assertEquals("application/json", request.header("Accept"))
        assertEquals("Bearer $AccessToken", request.header("Authorization"))
    }

    @Test
    fun `intercept trims access token before adding authorization header`() {
        val chain = mockChainWithCapturedRequest()

        AuthInterceptor(accessToken = "  $AccessToken  ").intercept(chain.value)

        val request = chain.proceededRequest.captured
        assertEquals("Bearer $AccessToken", request.header("Authorization"))
    }

    @Test
    fun `intercept throws missing token when access token is blank`() {
        val chain = mockChainWithCapturedRequest()

        assertThrows(AppException.MissingAccessToken::class.java) {
            AuthInterceptor(accessToken = " ").intercept(chain.value)
        }
        verify(exactly = 0) { chain.value.proceed(any()) }
    }

    @Test
    fun `intercept throws invalid token type when access token looks like legacy api key`() {
        val chain = mockChainWithCapturedRequest()

        assertThrows(AppException.InvalidAccessTokenType::class.java) {
            AuthInterceptor(accessToken = LegacyApiKey).intercept(chain.value)
        }
        verify(exactly = 0) { chain.value.proceed(any()) }
    }

    private fun mockChainWithCapturedRequest(): MockChain {
        val chain = mockk<Interceptor.Chain>()
        val proceededRequest = slot<Request>()
        every { chain.request() } returns Request.Builder()
            .url("https://api.themoviedb.org/3/genre/movie/list")
            .build()
        every { chain.proceed(capture(proceededRequest)) } answers {
            Response.Builder()
                .request(proceededRequest.captured)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .build()
        }
        return MockChain(
            value = chain,
            proceededRequest = proceededRequest
        )
    }

    private data class MockChain(
        val value: Interceptor.Chain,
        val proceededRequest: CapturingSlot<Request>
    )

    private companion object {
        const val AccessToken = "tmdb-read-access-token"
        const val LegacyApiKey = "0123456789abcdef0123456789abcdef"
    }
}
