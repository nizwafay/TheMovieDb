package com.papay.themoviedb.core.network.genre

import com.papay.themoviedb.core.network.NetworkFactory
import org.junit.Assert.assertEquals
import org.junit.Test

class GenreResponseDtoTest {
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
}
