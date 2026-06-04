package com.papay.themoviedb.core.network.movie

import com.papay.themoviedb.core.network.NetworkFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MovieDetailDtoTest {
    private val moshi = NetworkFactory.createMoshi()

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
}
