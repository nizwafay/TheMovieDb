package com.papay.themoviedb.core.domain.usecase

import com.papay.themoviedb.core.domain.repository.MovieRepository
import com.papay.themoviedb.core.model.MovieReview
import com.papay.themoviedb.core.model.MovieReviewPage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMovieReviewsUseCaseTest {
    private val movieRepository = mockk<MovieRepository>()
    private val getMovieReviews = GetMovieReviewsUseCase(movieRepository = movieRepository)

    @Test
    fun `invoke returns movie reviews from repository`() = runTest {
        coEvery {
            movieRepository.getMovieReviews(movieId = MovieId, page = Page)
        } returns MovieReviewPage

        val result = getMovieReviews(movieId = MovieId, page = Page)

        assertEquals(MovieReviewPage, result)
        coVerify(exactly = 1) {
            movieRepository.getMovieReviews(movieId = MovieId, page = Page)
        }
    }

    private companion object {
        const val MovieId = 550
        const val Page = 2

        val MovieReviewPage = MovieReviewPage(
            reviews = listOf(
                MovieReview(
                    id = "review-1",
                    author = "Reviewer",
                    content = "Great movie.",
                    createdAt = "2026-06-04T02:43:21.000Z",
                    rating = 8.5
                )
            ),
            page = Page,
            totalPages = 3,
            totalResults = 25
        )
    }
}
