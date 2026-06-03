package com.papay.themoviedb.core.data.movie.datasource

import com.papay.themoviedb.core.model.MoviePage
import com.papay.themoviedb.core.model.MovieVideo

interface MovieRemoteDataSource {
    suspend fun getMoviesByGenre(genreId: Int, page: Int): MoviePage
    suspend fun getMovieVideos(movieId: Int): List<MovieVideo>
}
