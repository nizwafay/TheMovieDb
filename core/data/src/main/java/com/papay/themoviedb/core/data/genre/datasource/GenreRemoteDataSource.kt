package com.papay.themoviedb.core.data.genre.datasource

import com.papay.themoviedb.core.model.Genre

interface GenreRemoteDataSource {
    suspend fun getGenres(): List<Genre>
}
