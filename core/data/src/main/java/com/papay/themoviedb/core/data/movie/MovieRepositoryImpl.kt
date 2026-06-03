package com.papay.themoviedb.core.data.movie

import com.papay.themoviedb.core.data.coroutine.DispatcherProvider
import com.papay.themoviedb.core.data.movie.datasource.MovieLocalDataSource
import com.papay.themoviedb.core.data.movie.datasource.MovieRemoteDataSource
import com.papay.themoviedb.core.domain.repository.MovieRepository
import com.papay.themoviedb.core.domain.result.DataResult
import com.papay.themoviedb.core.model.Movie
import com.papay.themoviedb.core.model.MovieDetail
import com.papay.themoviedb.core.model.MoviePage
import com.papay.themoviedb.core.model.MovieReviewPage
import com.papay.themoviedb.core.model.MovieVideo
import kotlinx.coroutines.withContext

class MovieRepositoryImpl(
    private val movieLocalDataSource: MovieLocalDataSource,
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val dispatcherProvider: DispatcherProvider
) : MovieRepository {
    override suspend fun getMovie(id: Int): Movie? = withContext(dispatcherProvider.io) {
        movieLocalDataSource.getMovie(id = id)
    }

    override suspend fun getMovieDetail(movieId: Int): MovieDetail = withContext(dispatcherProvider.io) {
        movieRemoteDataSource.getMovieDetail(movieId = movieId)
    }

    override suspend fun getMovieReviews(movieId: Int, page: Int): MovieReviewPage = withContext(dispatcherProvider.io) {
        movieRemoteDataSource.getMovieReviews(
            movieId = movieId,
            page = page
        )
    }

    override suspend fun getMoviesByGenre(genreId: Int, page: Int): DataResult<MoviePage> = withContext(dispatcherProvider.io) {
        val remoteResult = runCatching {
            movieRemoteDataSource.getMoviesByGenre(
                genreId = genreId,
                page = page
            )
        }.onSuccess { moviePage ->
            movieLocalDataSource.upsertMovies(
                genreId = genreId,
                movies = moviePage.movies
            )
        }

        val cachedMovies = if (page == FirstPage) {
            movieLocalDataSource.getMoviesByGenre(genreId = genreId)
        } else {
            emptyList()
        }

        if (cachedMovies.isNotEmpty()) {
            DataResult(
                data = MoviePage(
                    movies = cachedMovies,
                    page = FirstPage,
                    totalPages = remoteResult.getOrNull()?.totalPages ?: FirstPage
                ),
                fallbackError = remoteResult.exceptionOrNull()
            )
        } else {
            DataResult(data = remoteResult.getOrThrow())
        }
    }

    private companion object {
        const val FirstPage = 1
    }

    override suspend fun getMovieVideos(movieId: Int): List<MovieVideo> = withContext(dispatcherProvider.io) {
        movieRemoteDataSource.getMovieVideos(movieId = movieId)
    }
}
