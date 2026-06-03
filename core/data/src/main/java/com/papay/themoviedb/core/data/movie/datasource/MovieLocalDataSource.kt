package com.papay.themoviedb.core.data.movie.datasource

import com.papay.themoviedb.core.model.Movie

interface MovieLocalDataSource {
    suspend fun getMoviesByGenre(genreId: Int): List<Movie>
    suspend fun getMovie(id: Int): Movie?
    suspend fun upsertMovies(genreId: Int, movies: List<Movie>)
}
