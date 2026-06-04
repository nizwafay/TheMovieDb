package com.papay.themoviedb.core.network.movie

import com.papay.themoviedb.core.network.MockWebServerRule
import com.papay.themoviedb.core.network.enqueueJson
import com.papay.themoviedb.core.network.takeRecordedRequest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MovieApiServiceTest {
    @get:Rule
    val mockWebServerRule = MockWebServerRule()

    private val server get() = mockWebServerRule.server
    private val movieApiService by lazy { mockWebServerRule.createService<MovieApiService>() }

    @Test
    fun `getMoviesByGenre requests discover movie with default query params`() = runTest {
        server.enqueueJson("""{"page":1,"total_pages":1,"results":[]}""")

        movieApiService.getMoviesByGenre(genreId = 28)

        val request = server.takeRecordedRequest()
        assertEquals("/discover/movie", request.url.encodedPath)
        assertEquals("28", request.url.queryParameter("with_genres"))
        assertEquals("en-US", request.url.queryParameter("language"))
        assertEquals("popularity.desc", request.url.queryParameter("sort_by"))
        assertEquals("1", request.url.queryParameter("page"))
    }

    @Test
    fun `getMoviesByGenre requests discover movie with provided query params`() = runTest {
        server.enqueueJson("""{"page":3,"total_pages":3,"results":[]}""")

        movieApiService.getMoviesByGenre(
            genreId = 35,
            language = "id-ID",
            sortBy = "vote_average.desc",
            page = 3
        )

        val request = server.takeRecordedRequest()
        assertEquals("/discover/movie", request.url.encodedPath)
        assertEquals("35", request.url.queryParameter("with_genres"))
        assertEquals("id-ID", request.url.queryParameter("language"))
        assertEquals("vote_average.desc", request.url.queryParameter("sort_by"))
        assertEquals("3", request.url.queryParameter("page"))
    }

    @Test
    fun `getMovieDetail requests movie detail path with default language`() = runTest {
        server.enqueueJson(MovieDetailJson)

        movieApiService.getMovieDetail(movieId = MovieId)

        val request = server.takeRecordedRequest()
        assertEquals("/movie/$MovieId", request.url.encodedPath)
        assertEquals("en-US", request.url.queryParameter("language"))
    }

    @Test
    fun `getMovieDetail requests movie detail path with provided language`() = runTest {
        server.enqueueJson(MovieDetailJson)

        movieApiService.getMovieDetail(movieId = MovieId, language = "id-ID")

        val request = server.takeRecordedRequest()
        assertEquals("/movie/$MovieId", request.url.encodedPath)
        assertEquals("id-ID", request.url.queryParameter("language"))
    }

    @Test
    fun `getMovieReviews requests movie reviews path with default page`() = runTest {
        server.enqueueJson("""{"page":1,"total_pages":1,"total_results":0,"results":[]}""")

        movieApiService.getMovieReviews(movieId = MovieId)

        val request = server.takeRecordedRequest()
        assertEquals("/movie/$MovieId/reviews", request.url.encodedPath)
        assertEquals("1", request.url.queryParameter("page"))
    }

    @Test
    fun `getMovieReviews requests movie reviews path with provided page`() = runTest {
        server.enqueueJson("""{"page":2,"total_pages":2,"total_results":20,"results":[]}""")

        movieApiService.getMovieReviews(movieId = MovieId, page = 2)

        val request = server.takeRecordedRequest()
        assertEquals("/movie/$MovieId/reviews", request.url.encodedPath)
        assertEquals("2", request.url.queryParameter("page"))
    }

    @Test
    fun `getMovieVideos requests movie videos path with default language`() = runTest {
        server.enqueueJson("""{"results":[]}""")

        movieApiService.getMovieVideos(movieId = MovieId)

        val request = server.takeRecordedRequest()
        assertEquals("/movie/$MovieId/videos", request.url.encodedPath)
        assertEquals("en-US", request.url.queryParameter("language"))
    }

    @Test
    fun `getMovieVideos requests movie videos path with provided language`() = runTest {
        server.enqueueJson("""{"results":[]}""")

        movieApiService.getMovieVideos(movieId = MovieId, language = "id-ID")

        val request = server.takeRecordedRequest()
        assertEquals("/movie/$MovieId/videos", request.url.encodedPath)
        assertEquals("id-ID", request.url.queryParameter("language"))
    }

    private companion object {
        const val MovieId = 550

        const val MovieDetailJson = """
            {
              "id": 550,
              "title": "Fight Club",
              "overview": "An insomniac office worker meets a soap maker.",
              "release_date": "1999-10-15",
              "genres": [],
              "spoken_languages": [],
              "vote_average": 8.4,
              "vote_count": 12000
            }
        """
    }
}
