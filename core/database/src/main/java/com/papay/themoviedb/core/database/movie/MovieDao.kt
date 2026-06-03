package com.papay.themoviedb.core.database.movie

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies WHERE genreId = :genreId")
    suspend fun getMoviesByGenre(genreId: Int): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    suspend fun getMovie(id: Int): MovieEntity?

    @Upsert
    suspend fun upsertMovies(movies: List<MovieEntity>)
}
