package com.papay.themoviedb.core.domain.repository

import com.papay.themoviedb.core.model.Genre
import com.papay.themoviedb.core.domain.result.DataResult

interface GenreRepository {
    suspend fun getGenres(): DataResult<List<Genre>>
}
