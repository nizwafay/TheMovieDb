package com.papay.themoviedb.core.network.movie

import com.papay.themoviedb.core.network.NetworkFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MovieResponseDtoTest {
    private val moshi = NetworkFactory.createMoshi()

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
}
