package com.papay.themoviedb.core.domain.usecase

import com.papay.themoviedb.core.domain.repository.MovieRepository
import com.papay.themoviedb.core.model.Movie
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetMovieUseCaseTest {
    private val movieRepository = mockk<MovieRepository>()
    private val getMovie = GetMovieUseCase(movieRepository = movieRepository)

    @Test
    fun `invoke returns movie from repository`() = runTest {
        coEvery { movieRepository.getMovie(MovieId) } returns Movie

        val result = getMovie(id = MovieId)

        assertEquals(Movie, result)
        coVerify(exactly = 1) { movieRepository.getMovie(MovieId) }
    }

    @Test
    fun `invoke returns null when repository has no movie`() = runTest {
        coEvery { movieRepository.getMovie(MovieId) } returns null

        val result = getMovie(id = MovieId)

        assertNull(result)
        coVerify(exactly = 1) { movieRepository.getMovie(MovieId) }
    }

    private companion object {
        const val MovieId = 550

        val Movie = Movie(
            id = MovieId,
            title = "Fight Club",
            posterPath = "/poster.jpg",
            releaseDate = "1999-10-15"
        )
    }
}
