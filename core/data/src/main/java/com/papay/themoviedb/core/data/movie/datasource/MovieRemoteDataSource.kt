package com.papay.themoviedb.core.data.movie.datasource

import com.papay.themoviedb.core.model.MovieDetail
import com.papay.themoviedb.core.model.MoviePage
import com.papay.themoviedb.core.model.MovieReviewPage
import com.papay.themoviedb.core.model.MovieVideo

interface MovieRemoteDataSource {
    suspend fun getMoviesByGenre(genreId: Int, page: Int): MoviePage
    suspend fun getMovieDetail(movieId: Int): MovieDetail
    suspend fun getMovieReviews(movieId: Int, page: Int): MovieReviewPage
    suspend fun getMovieVideos(movieId: Int): List<MovieVideo>
}
