package com.papay.themoviedb.core.data.movie.remote

import com.papay.themoviedb.core.data.movie.datasource.MovieRemoteDataSource
import com.papay.themoviedb.core.data.movie.toModel
import com.papay.themoviedb.core.model.MovieDetail
import com.papay.themoviedb.core.model.MoviePage
import com.papay.themoviedb.core.model.MovieVideo
import com.papay.themoviedb.core.network.RetrofitRemoteDataSource
import com.papay.themoviedb.core.network.movie.MovieApiService

class MovieRemoteDataSourceImpl(
    private val movieApiService: MovieApiService,
    private val retrofitRemoteDataSource: RetrofitRemoteDataSource
) : MovieRemoteDataSource {
    override suspend fun getMoviesByGenre(genreId: Int, page: Int): MoviePage {
        return retrofitRemoteDataSource.execute {
            movieApiService.getMoviesByGenre(
                genreId = genreId,
                page = page
            )
        }.toModel()
    }

    override suspend fun getMovieDetail(movieId: Int): MovieDetail {
        return retrofitRemoteDataSource.execute {
            movieApiService.getMovieDetail(movieId = movieId)
        }.toModel()
    }

    override suspend fun getMovieVideos(movieId: Int): List<MovieVideo> {
        return retrofitRemoteDataSource.execute {
            movieApiService.getMovieVideos(movieId = movieId)
        }.results
            .map { video -> video.toModel() }
    }
}
