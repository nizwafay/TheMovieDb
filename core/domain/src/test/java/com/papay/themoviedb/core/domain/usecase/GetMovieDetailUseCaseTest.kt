package com.papay.themoviedb.core.domain.usecase

import com.papay.themoviedb.core.domain.repository.MovieRepository
import com.papay.themoviedb.core.model.Genre
import com.papay.themoviedb.core.model.MovieDetail
import com.papay.themoviedb.core.model.SpokenLanguage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMovieDetailUseCaseTest {
    private val movieRepository = mockk<MovieRepository>()
    private val getMovieDetail = GetMovieDetailUseCase(movieRepository = movieRepository)

    @Test
    fun `invoke returns movie detail from repository`() = runTest {
        coEvery { movieRepository.getMovieDetail(MovieId) } returns MovieDetail

        val result = getMovieDetail(movieId = MovieId)

        assertEquals(MovieDetail, result)
        coVerify(exactly = 1) { movieRepository.getMovieDetail(MovieId) }
    }

    private companion object {
        const val MovieId = 550

        val MovieDetail = MovieDetail(
            id = MovieId,
            title = "Fight Club",
            overview = "An insomniac office worker meets a soap maker.",
            releaseDate = "1999-10-15",
            genres = listOf(Genre(id = 18, name = "Drama")),
            spokenLanguages = listOf(SpokenLanguage(name = "English")),
            voteAverage = 8.4,
            voteCount = 12000
        )
    }
}
