package com.papay.themoviedb.core.network.genre

import com.papay.themoviedb.core.network.MockWebServerRule
import com.papay.themoviedb.core.network.enqueueJson
import com.papay.themoviedb.core.network.takeRecordedRequest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GenreApiServiceTest {
    @get:Rule
    val mockWebServerRule = MockWebServerRule()

    private val server get() = mockWebServerRule.server
    private val genreApiService by lazy { mockWebServerRule.createService<GenreApiService>() }

    @Test
    fun `getGenres requests genre movie list with default language`() = runTest {
        server.enqueueJson("""{"genres":[]}""")

        genreApiService.getGenres()

        val request = server.takeRecordedRequest()
        assertEquals("/genre/movie/list", request.url.encodedPath)
        assertEquals("en-US", request.url.queryParameter("language"))
    }

    @Test
    fun `getGenres requests genre movie list with provided language`() = runTest {
        server.enqueueJson("""{"genres":[]}""")

        genreApiService.getGenres(language = "id-ID")

        val request = server.takeRecordedRequest()
        assertEquals("/genre/movie/list", request.url.encodedPath)
        assertEquals("id-ID", request.url.queryParameter("language"))
    }

}
