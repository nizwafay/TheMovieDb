package com.papay.themoviedb.core.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkFactoryTest {
    @Test
    fun `createMoshi creates moshi that parses kotlin data classes`() {
        val moshi = NetworkFactory.createMoshi()

        val dto = requireNotNull(
            moshi.adapter(TestDto::class.java)
                .fromJson("""{"name":"The Movie DB"}""")
        )

        assertEquals("The Movie DB", dto.name)
    }

    @Test
    fun `createOkHttpClient adds auth interceptor`() {
        val okHttpClient = NetworkFactory.createOkHttpClient(accessToken = AccessToken)

        assertEquals(1, okHttpClient.interceptors.size)
        assertTrue(okHttpClient.interceptors.first() is AuthInterceptor)
    }

    @Test
    fun `createRetrofit uses network config base url and provided client`() {
        val okHttpClient = NetworkFactory.createOkHttpClient(accessToken = AccessToken)
        val retrofit = NetworkFactory.createRetrofit(
            moshi = NetworkFactory.createMoshi(),
            okHttpClient = okHttpClient
        )

        assertEquals(NetworkConfig.BaseUrl, retrofit.baseUrl().toString())
        assertSame(okHttpClient, retrofit.callFactory())
    }

    private data class TestDto(
        val name: String
    )

    private companion object {
        const val AccessToken = "tmdb-read-access-token"
    }
}
