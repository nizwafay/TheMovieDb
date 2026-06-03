package com.papay.themoviedb.core.data.genre.datasource

import com.papay.themoviedb.core.model.Genre

interface GenreLocalDataSource {
    suspend fun getGenres(): List<Genre>
    suspend fun upsertGenres(genres: List<Genre>)
}
