package com.papay.themoviedb.core.data.movie.local

import com.papay.themoviedb.core.database.movie.MovieDao
import com.papay.themoviedb.core.database.movie.MovieEntity
import com.papay.themoviedb.core.model.Movie
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MovieLocalDataSourceImplTest {
    private val movieDao = mockk<MovieDao>()
    private val dataSource = MovieLocalDataSourceImpl(movieDao = movieDao)

    @Test
    fun `get movies by genre returns mapped movies from dao`() = runTest {
        coEvery { movieDao.getMoviesByGenre(genreId = 28) } returns MovieEntities

        val result = dataSource.getMoviesByGenre(genreId = 28)

        assertEquals(Movies, result)
    }

    @Test
    fun `get movie returns mapped movie from dao`() = runTest {
        coEvery { movieDao.getMovie(id = 1) } returns MovieEntities.first()

        val result = dataSource.getMovie(id = 1)

        assertEquals(Movies.first(), result)
    }

    @Test
    fun `get movie returns null when dao returns null`() = runTest {
        coEvery { movieDao.getMovie(id = 999) } returns null

        val result = dataSource.getMovie(id = 999)

        assertNull(result)
    }

    @Test
    fun `upsert movies saves mapped entities to dao`() = runTest {
        coEvery { movieDao.upsertMovies(MovieEntities) } returns Unit

        dataSource.upsertMovies(genreId = 28, movies = Movies)

        coVerify { movieDao.upsertMovies(MovieEntities) }
    }

    private companion object {
        val MovieEntities = listOf(
            MovieEntity(
                genreId = 28,
                id = 1,
                title = "Inception",
                posterPath = "/inception.jpg",
                releaseDate = "2010-07-16"
            ),
            MovieEntity(
                genreId = 28,
                id = 2,
                title = "Interstellar",
                posterPath = "/interstellar.jpg",
                releaseDate = "2014-11-07"
            )
        )
        val Movies = listOf(
            Movie(
                id = 1,
                title = "Inception",
                posterPath = "/inception.jpg",
                releaseDate = "2010-07-16"
            ),
            Movie(
                id = 2,
                title = "Interstellar",
                posterPath = "/interstellar.jpg",
                releaseDate = "2014-11-07"
            )
        )
    }
}
