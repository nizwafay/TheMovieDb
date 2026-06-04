package com.papay.themoviedb.core.domain.usecase

import com.papay.themoviedb.core.domain.error.AppException
import com.papay.themoviedb.core.domain.repository.MovieRepository
import com.papay.themoviedb.core.domain.result.DataResult
import com.papay.themoviedb.core.model.Movie
import com.papay.themoviedb.core.model.MoviePage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class GetMoviesByGenreUseCaseTest {
    private val movieRepository = mockk<MovieRepository>()
    private val getMoviesByGenre = GetMoviesByGenreUseCase(movieRepository = movieRepository)

    @Test
    fun `invoke returns movies by genre from repository`() = runTest {
        val dataResult = DataResult(data = MoviePage)
        coEvery {
            movieRepository.getMoviesByGenre(genreId = GenreId, page = Page)
        } returns dataResult

        val result = getMoviesByGenre(genreId = GenreId, page = Page)

        assertEquals(dataResult, result)
        coVerify(exactly = 1) {
            movieRepository.getMoviesByGenre(genreId = GenreId, page = Page)
        }
    }

    @Test
    fun `invoke keeps fallback error from repository`() = runTest {
        val fallbackError = AppException.NetworkUnavailable(cause = RuntimeException())
        coEvery {
            movieRepository.getMoviesByGenre(genreId = GenreId, page = Page)
        } returns DataResult(
            data = MoviePage,
            fallbackError = fallbackError
        )

        val result = getMoviesByGenre(genreId = GenreId, page = Page)

        assertEquals(MoviePage, result.data)
        assertSame(fallbackError, result.fallbackError)
        coVerify(exactly = 1) {
            movieRepository.getMoviesByGenre(genreId = GenreId, page = Page)
        }
    }

    private companion object {
        const val GenreId = 28
        const val Page = 2

        val MoviePage = MoviePage(
            movies = listOf(
                Movie(
                    id = 550,
                    title = "Fight Club",
                    posterPath = "/poster.jpg",
                    releaseDate = "1999-10-15"
                )
            ),
            page = Page,
            totalPages = 3
        )
    }
}
