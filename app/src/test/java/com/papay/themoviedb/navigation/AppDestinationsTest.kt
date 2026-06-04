package com.papay.themoviedb.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class AppDestinationsTest {

    @Test
    fun `movies by genre creates route with genre id`() {
        val result = AppDestinations.moviesByGenre(genreId = 28)

        assertEquals("movies/28", result)
    }

    @Test
    fun `movie detail creates route with movie id`() {
        val result = AppDestinations.movieDetail(movieId = 550)

        assertEquals("movies/detail/550", result)
    }

    @Test
    fun `route templates use expected arguments`() {
        assertEquals("genres", AppDestinations.Genres)
        assertEquals("genreId", AppDestinations.MovieGenreIdArg)
        assertEquals("movieId", AppDestinations.MovieIdArg)
        assertEquals("movies/{genreId}", AppDestinations.MoviesByGenre)
        assertEquals("movies/detail/{movieId}", AppDestinations.MovieDetail)
    }
}
