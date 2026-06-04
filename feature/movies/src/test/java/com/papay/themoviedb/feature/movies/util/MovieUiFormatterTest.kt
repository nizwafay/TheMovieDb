package com.papay.themoviedb.feature.movies.util

import com.papay.themoviedb.core.model.Movie
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MovieUiFormatterTest {
    @Test
    fun `posterUrl returns TMDB poster url when poster path exists`() {
        val movie = movie(posterPath = "/poster.jpg")

        assertEquals("https://image.tmdb.org/t/p/w342/poster.jpg", movie.posterUrl())
    }

    @Test
    fun `posterUrl returns null when poster path is null`() {
        val movie = movie(posterPath = null)

        assertNull(movie.posterUrl())
    }

    @Test
    fun `releaseYear returns year from release date`() {
        val movie = movie(releaseDate = "2026-06-04")

        assertEquals("2026", movie.releaseYear())
    }

    @Test
    fun `releaseYear returns first four characters when release date has no separators`() {
        val movie = movie(releaseDate = "20260604")

        assertEquals("2026", movie.releaseYear())
    }

    @Test
    fun `releaseYear returns null when release date is too short`() {
        val movie = movie(releaseDate = "202")

        assertNull(movie.releaseYear())
    }

    @Test
    fun `releaseYear returns null when release date is null`() {
        val movie = movie(releaseDate = null)

        assertNull(movie.releaseYear())
    }

    private fun movie(
        posterPath: String? = "/poster.jpg",
        releaseDate: String? = "2026-06-04"
    ): Movie {
        return Movie(
            id = 1,
            title = "Movie",
            posterPath = posterPath,
            releaseDate = releaseDate
        )
    }
}
