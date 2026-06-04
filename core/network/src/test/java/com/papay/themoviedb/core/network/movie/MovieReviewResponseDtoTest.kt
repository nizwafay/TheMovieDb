package com.papay.themoviedb.core.network.movie

import com.papay.themoviedb.core.network.NetworkFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MovieReviewResponseDtoTest {
    private val moshi = NetworkFactory.createMoshi()

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
