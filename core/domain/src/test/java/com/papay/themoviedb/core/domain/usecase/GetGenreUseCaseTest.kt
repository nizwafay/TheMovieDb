package com.papay.themoviedb.core.domain.usecase

import com.papay.themoviedb.core.domain.repository.GenreRepository
import com.papay.themoviedb.core.model.Genre
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetGenreUseCaseTest {
    private val genreRepository = mockk<GenreRepository>()
    private val getGenre = GetGenreUseCase(genreRepository = genreRepository)

    @Test
    fun `invoke returns genre from repository`() = runTest {
        coEvery { genreRepository.getGenre(GenreId) } returns Genre

        val result = getGenre(id = GenreId)

        assertEquals(Genre, result)
        coVerify(exactly = 1) { genreRepository.getGenre(GenreId) }
    }

    @Test
    fun `invoke returns null when repository has no genre`() = runTest {
        coEvery { genreRepository.getGenre(GenreId) } returns null

        val result = getGenre(id = GenreId)

        assertNull(result)
        coVerify(exactly = 1) { genreRepository.getGenre(GenreId) }
    }

    private companion object {
        const val GenreId = 28

        val Genre = Genre(
            id = GenreId,
            name = "Action"
        )
    }
}
