package com.papay.themoviedb.core.data.movie.local

import com.papay.themoviedb.core.data.movie.datasource.MovieLocalDataSource
import com.papay.themoviedb.core.data.movie.toEntity
import com.papay.themoviedb.core.data.movie.toModel
import com.papay.themoviedb.core.database.movie.MovieDao
import com.papay.themoviedb.core.model.Movie

class MovieLocalDataSourceImpl(
    private val movieDao: MovieDao
) : MovieLocalDataSource {
    override suspend fun getMoviesByGenre(genreId: Int): List<Movie> {
        return movieDao.getMoviesByGenre(genreId = genreId).map { movie -> movie.toModel() }
    }

    override suspend fun upsertMovies(genreId: Int, movies: List<Movie>) {
        movieDao.upsertMovies(movies.map { movie -> movie.toEntity(genreId = genreId) })
    }
}
