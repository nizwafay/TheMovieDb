package com.papay.themoviedb.core.data.genre.remote

import com.papay.themoviedb.core.model.Genre
import com.papay.themoviedb.core.network.RetrofitRemoteDataSource
import com.papay.themoviedb.core.network.genre.GenreApiService
import com.papay.themoviedb.core.network.genre.GenreDto
import com.papay.themoviedb.core.network.genre.GenreResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GenreRemoteDataSourceImplTest {
    private val genreApiService = mockk<GenreApiService>()
    private val dataSource = GenreRemoteDataSourceImpl(
        genreApiService = genreApiService,
        retrofitRemoteDataSource = RetrofitRemoteDataSource()
    )

    @Test
    fun `get genres returns mapped genres from api`() = runTest {
        coEvery { genreApiService.getGenres() } returns GenreResponse

        val result = dataSource.getGenres()

        assertEquals(Genres, result)
        coVerify { genreApiService.getGenres() }
    }

    private companion object {
        val GenreResponse = GenreResponseDto(
            genres = listOf(
                GenreDto(id = 28, name = "Action"),
                GenreDto(id = 35, name = "Comedy")
            )
        )
        val Genres = listOf(
            Genre(id = 28, name = "Action"),
            Genre(id = 35, name = "Comedy")
        )
    }
}
