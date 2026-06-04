package com.papay.themoviedb.core.network.movie

import com.papay.themoviedb.core.network.NetworkFactory
import org.junit.Assert.assertEquals
import org.junit.Test

class MovieVideoResponseDtoTest {
    private val moshi = NetworkFactory.createMoshi()

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
}
