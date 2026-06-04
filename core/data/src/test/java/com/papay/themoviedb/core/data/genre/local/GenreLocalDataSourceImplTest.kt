package com.papay.themoviedb.core.data.genre.local

import com.papay.themoviedb.core.database.genre.GenreDao
import com.papay.themoviedb.core.database.genre.GenreEntity
import com.papay.themoviedb.core.model.Genre
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GenreLocalDataSourceImplTest {
    private val genreDao = mockk<GenreDao>()
    private val dataSource = GenreLocalDataSourceImpl(genreDao = genreDao)

    @Test
    fun `get genres returns mapped genres from dao`() = runTest {
        coEvery { genreDao.getGenres() } returns GenreEntities

        val result = dataSource.getGenres()

        assertEquals(Genres, result)
    }

    @Test
    fun `get genre returns mapped genre from dao`() = runTest {
        coEvery { genreDao.getGenre(id = 28) } returns GenreEntities.first()

        val result = dataSource.getGenre(id = 28)

        assertEquals(Genres.first(), result)
    }

    @Test
    fun `get genre returns null when dao returns null`() = runTest {
        coEvery { genreDao.getGenre(id = 999) } returns null

        val result = dataSource.getGenre(id = 999)

        assertNull(result)
    }

    @Test
    fun `upsert genres saves mapped entities to dao`() = runTest {
        coEvery { genreDao.upsertGenres(GenreEntities) } returns Unit

        dataSource.upsertGenres(Genres)

        coVerify { genreDao.upsertGenres(GenreEntities) }
    }

    private companion object {
        val GenreEntities = listOf(
            GenreEntity(id = 28, name = "Action"),
            GenreEntity(id = 35, name = "Comedy")
        )
        val Genres = listOf(
            Genre(id = 28, name = "Action"),
            Genre(id = 35, name = "Comedy")
        )
    }
}
