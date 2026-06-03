package com.papay.themoviedb.core.domain.repository

import com.papay.themoviedb.core.domain.result.DataResult
import com.papay.themoviedb.core.model.Movie
import com.papay.themoviedb.core.model.MovieDetail
import com.papay.themoviedb.core.model.MoviePage
import com.papay.themoviedb.core.model.MovieVideo

interface MovieRepository {
    suspend fun getMovie(id: Int): Movie?
    suspend fun getMovieDetail(movieId: Int): MovieDetail
    suspend fun getMoviesByGenre(genreId: Int, page: Int): DataResult<MoviePage>
    suspend fun getMovieVideos(movieId: Int): List<MovieVideo>
}
