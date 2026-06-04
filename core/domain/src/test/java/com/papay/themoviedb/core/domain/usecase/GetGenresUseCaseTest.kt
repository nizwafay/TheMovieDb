package com.papay.themoviedb.core.domain.usecase

import com.papay.themoviedb.core.domain.error.AppException
import com.papay.themoviedb.core.domain.repository.GenreRepository
import com.papay.themoviedb.core.domain.result.DataResult
import com.papay.themoviedb.core.model.Genre
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class GetGenresUseCaseTest {
    private val genreRepository = mockk<GenreRepository>()
    private val getGenres = GetGenresUseCase(genreRepository = genreRepository)

    @Test
    fun `invoke returns genres from repository`() = runTest {
        val dataResult = DataResult(data = Genres)
        coEvery { genreRepository.getGenres() } returns dataResult

        val result = getGenres()

        assertEquals(dataResult, result)
        coVerify(exactly = 1) { genreRepository.getGenres() }
    }

    @Test
    fun `invoke returns empty genres from repository`() = runTest {
        val dataResult = DataResult(data = emptyList<Genre>())
        coEvery { genreRepository.getGenres() } returns dataResult

        val result = getGenres()

        assertEquals(dataResult, result)
        coVerify(exactly = 1) { genreRepository.getGenres() }
    }

    @Test
    fun `invoke keeps fallback error from repository`() = runTest {
        val fallbackError = AppException.RateLimited()
        coEvery { genreRepository.getGenres() } returns DataResult(
            data = Genres,
            fallbackError = fallbackError
        )

        val result = getGenres()

        assertEquals(Genres, result.data)
        assertSame(fallbackError, result.fallbackError)
        coVerify(exactly = 1) { genreRepository.getGenres() }
    }

    private companion object {
        val Genres = listOf(
            Genre(id = 28, name = "Action"),
            Genre(id = 35, name = "Comedy")
        )
    }
}
