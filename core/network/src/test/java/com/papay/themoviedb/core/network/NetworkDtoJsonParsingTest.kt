package com.papay.themoviedb.core.network

import com.papay.themoviedb.core.network.genre.GenreResponseDto
import com.papay.themoviedb.core.network.movie.MovieDetailDto
import com.papay.themoviedb.core.network.movie.MovieResponseDto
import com.papay.themoviedb.core.network.movie.MovieReviewResponseDto
import com.papay.themoviedb.core.network.movie.MovieVideoResponseDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NetworkDtoJsonParsingTest {
    private val moshi = NetworkFactory.createMoshi()

    @Test
    fun `genre response parses genres`() {
        val json = """
            {
              "genres": [
                {
                  "id": 28,
                  "name": "Action"
                }
              ]
            }
        """.trimIndent()

        val response = requireNotNull(moshi.adapter(GenreResponseDto::class.java).fromJson(json))

        assertEquals(1, response.genres.size)
        assertEquals(28, response.genres.first().id)
        assertEquals("Action", response.genres.first().name)
    }

    @Test
    fun `movie response parses snake case movie fields`() {
        val json = """
            {
              "page": 1,
              "total_pages": 3,
              "results": [
                {
                  "id": 550,
                  "title": "Fight Club",
                  "poster_path": "/poster.jpg",
                  "release_date": "1999-10-15"
                }
              ]
            }
        """.trimIndent()

        val response = requireNotNull(moshi.adapter(MovieResponseDto::class.java).fromJson(json))
        val movie = response.results.first()

        assertEquals(1, response.page)
        assertEquals(3, response.totalPages)
        assertEquals(550, movie.id)
        assertEquals("Fight Club", movie.title)
        assertEquals("/poster.jpg", movie.posterPath)
        assertEquals("1999-10-15", movie.releaseDate)
    }

    @Test
    fun `movie response parses nullable movie fields`() {
        val json = """
            {
              "page": 1,
              "total_pages": 1,
              "results": [
                {
                  "id": 1,
                  "title": "Untitled",
                  "poster_path": null,
                  "release_date": null
                }
              ]
            }
        """.trimIndent()

        val response = requireNotNull(moshi.adapter(MovieResponseDto::class.java).fromJson(json))
        val movie = response.results.first()

        assertNull(movie.posterPath)
        assertNull(movie.releaseDate)
    }

    @Test
    fun `movie detail parses snake case detail fields`() {
        val json = """
            {
              "id": 550,
              "title": "Fight Club",
              "overview": "An insomniac office worker meets a soap maker.",
              "release_date": "1999-10-15",
              "genres": [
                {
                  "id": 18,
                  "name": "Drama"
                }
              ],
              "spoken_languages": [
                {
                  "english_name": "English",
                  "name": "English"
                }
              ],
              "vote_average": 8.4,
              "vote_count": 12000
            }
        """.trimIndent()

        val detail = requireNotNull(moshi.adapter(MovieDetailDto::class.java).fromJson(json))

        assertEquals(550, detail.id)
        assertEquals("Fight Club", detail.title)
        assertEquals("An insomniac office worker meets a soap maker.", detail.overview)
        assertEquals("1999-10-15", detail.releaseDate)
        assertEquals(18, detail.genres.first().id)
        assertEquals("Drama", detail.genres.first().name)
        assertEquals("English", detail.spokenLanguages.first().englishName)
        assertEquals("English", detail.spokenLanguages.first().name)
        assertEquals(8.4, requireNotNull(detail.voteAverage), 0.0)
        assertEquals(12000, detail.voteCount)
    }

    @Test
    fun `movie detail parses nullable optional fields`() {
        val json = """
            {
              "id": 1,
              "title": "Movie",
              "overview": "",
              "release_date": null,
              "genres": [],
              "spoken_languages": [
                {
                  "english_name": null,
                  "name": null
                }
              ],
              "vote_average": null,
              "vote_count": null
            }
        """.trimIndent()

        val detail = requireNotNull(moshi.adapter(MovieDetailDto::class.java).fromJson(json))

        assertNull(detail.releaseDate)
        assertNull(detail.spokenLanguages.first().englishName)
        assertNull(detail.spokenLanguages.first().name)
        assertNull(detail.voteAverage)
        assertNull(detail.voteCount)
    }

    @Test
    fun `movie video response parses videos`() {
        val json = """
            {
              "results": [
                {
                  "key": "abc123",
                  "name": "Official Trailer",
                  "site": "YouTube",
                  "type": "Trailer"
                }
              ]
            }
        """.trimIndent()

        val response = requireNotNull(moshi.adapter(MovieVideoResponseDto::class.java).fromJson(json))
        val video = response.results.first()

        assertEquals("abc123", video.key)
        assertEquals("Official Trailer", video.name)
        assertEquals("YouTube", video.site)
        assertEquals("Trailer", video.type)
    }

    @Test
    fun `movie review response parses snake case review fields`() {
        val json = """
            {
              "page": 1,
              "total_pages": 2,
              "total_results": 25,
              "results": [
                {
                  "id": "review-1",
                  "author": "Reviewer",
                  "content": "Great movie.",
                  "created_at": "2026-06-04T02:43:21.000Z",
                  "author_details": {
                    "rating": 8.5
                  }
                }
              ]
            }
        """.trimIndent()

        val response = requireNotNull(moshi.adapter(MovieReviewResponseDto::class.java).fromJson(json))
        val review = response.results.first()

        assertEquals(1, response.page)
        assertEquals(2, response.totalPages)
        assertEquals(25, response.totalResults)
        assertEquals("review-1", review.id)
        assertEquals("Reviewer", review.author)
        assertEquals("Great movie.", review.content)
        assertEquals("2026-06-04T02:43:21.000Z", review.createdAt)
        assertEquals(8.5, requireNotNull(review.authorDetails?.rating), 0.0)
    }

    @Test
    fun `movie review response parses nullable review fields`() {
        val json = """
            {
              "page": 1,
              "total_pages": 1,
              "total_results": 1,
              "results": [
                {
                  "id": "review-1",
                  "author": "Reviewer",
                  "content": "Great movie.",
                  "created_at": null,
                  "author_details": null
                }
              ]
            }
        """.trimIndent()

        val response = requireNotNull(moshi.adapter(MovieReviewResponseDto::class.java).fromJson(json))
        val review = response.results.first()

        assertNull(review.createdAt)
        assertNull(review.authorDetails)
    }
}
