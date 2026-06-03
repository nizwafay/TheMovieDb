package com.papay.themoviedb.core.data.movie.datasource

import com.papay.themoviedb.core.model.MoviePage

interface MovieRemoteDataSource {
    suspend fun getMoviesByGenre(genreId: Int, page: Int): MoviePage
}
