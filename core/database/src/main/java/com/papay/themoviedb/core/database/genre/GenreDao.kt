package com.papay.themoviedb.core.database.genre

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface GenreDao {
    @Query("SELECT * FROM genres")
    suspend fun getGenres(): List<GenreEntity>

    @Query("SELECT * FROM genres WHERE id = :id LIMIT 1")
    suspend fun getGenre(id: Int): GenreEntity?

    @Upsert
    suspend fun upsertGenres(genres: List<GenreEntity>)
}
