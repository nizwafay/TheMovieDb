package com.papay.themoviedb.core.domain.repository

import com.papay.themoviedb.core.domain.result.DataResult
import com.papay.themoviedb.core.model.MoviePage

interface MovieRepository {
    suspend fun getMoviesByGenre(genreId: Int, page: Int): DataResult<MoviePage>
}
