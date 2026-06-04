package com.papay.themoviedb.core.data.movie.remote

import com.papay.themoviedb.core.model.Genre
import com.papay.themoviedb.core.model.Movie
import com.papay.themoviedb.core.model.MovieDetail
import com.papay.themoviedb.core.model.MoviePage
import com.papay.themoviedb.core.model.MovieReview
import com.papay.themoviedb.core.model.MovieReviewPage
import com.papay.themoviedb.core.model.MovieVideo
import com.papay.themoviedb.core.model.SpokenLanguage
import com.papay.themoviedb.core.network.RetrofitRemoteDataSource
import com.papay.themoviedb.core.network.genre.GenreDto
import com.papay.themoviedb.core.network.movie.AuthorDetailsDto
import com.papay.themoviedb.core.network.movie.MovieApiService
import com.papay.themoviedb.core.network.movie.MovieDetailDto
import com.papay.themoviedb.core.network.movie.MovieDto
import com.papay.themoviedb.core.network.movie.MovieResponseDto
import com.papay.themoviedb.core.network.movie.MovieReviewDto
import com.papay.themoviedb.core.network.movie.MovieReviewResponseDto
import com.papay.themoviedb.core.network.movie.MovieVideoDto
import com.papay.themoviedb.core.network.movie.MovieVideoResponseDto
import com.papay.themoviedb.core.network.movie.SpokenLanguageDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class MovieRemoteDataSourceImplTest {
    private val movieApiService = mockk<MovieApiService>()
    private val dataSource = MovieRemoteDataSourceImpl(
        movieApiService = movieApiService,
        retrofitRemoteDataSource = RetrofitRemoteDataSource()
    )

    @Test
    fun `get movies by genre returns mapped movie page from api`() = runTest {
        coEvery {
            movieApiService.getMoviesByGenre(genreId = 28, page = 2)
        } returns MovieResponse

        val result = dataSource.getMoviesByGenre(genreId = 28, page = 2)

        assertEquals(MoviePageModel, result)
        coVerify { movieApiService.getMoviesByGenre(genreId = 28, page = 2) }
    }

    @Test
    fun `get movie detail returns mapped detail from api`() = runTest {
        coEvery { movieApiService.getMovieDetail(movieId = 1) } returns MovieDetailResponse

        val result = dataSource.getMovieDetail(movieId = 1)

        assertEquals(MovieDetailModel, result)
        coVerify { movieApiService.getMovieDetail(movieId = 1) }
    }

    @Test
    fun `get movie reviews returns mapped review page from api`() = runTest {
        coEvery { movieApiService.getMovieReviews(movieId = 1, page = 3) } returns MovieReviewResponse

        val result = dataSource.getMovieReviews(movieId = 1, page = 3)

        assertEquals(MovieReviewPageModel, result)
        coVerify { movieApiService.getMovieReviews(movieId = 1, page = 3) }
    }

    @Test
    fun `get movie videos returns mapped videos from api`() = runTest {
        coEvery { movieApiService.getMovieVideos(movieId = 1) } returns MovieVideoResponse

        val result = dataSource.getMovieVideos(movieId = 1)

        assertEquals(MovieVideos, result)
        coVerify { movieApiService.getMovieVideos(movieId = 1) }
    }

    private companion object {
        val MovieResponse = MovieResponseDto(
            page = 2,
            totalPages = 10,
            results = listOf(MovieDto(id = 1, title = "Inception", posterPath = "/inception.jpg", releaseDate = "2010-07-16"))
        )
        val MoviePageModel = MoviePage(
            page = 2,
            totalPages = 10,
            movies = listOf(Movie(id = 1, title = "Inception", posterPath = "/inception.jpg", releaseDate = "2010-07-16"))
        )
        val MovieDetailResponse = MovieDetailDto(
            id = 1,
            title = "Inception",
            overview = "A thief steals corporate secrets through dream-sharing technology.",
            releaseDate = "2010-07-16",
            genres = listOf(GenreDto(id = 28, name = "Action")),
            spokenLanguages = listOf(SpokenLanguageDto(englishName = "English", name = "English")),
            voteAverage = 8.4,
            voteCount = 37000
        )
        val MovieDetailModel = MovieDetail(
            id = 1,
            title = "Inception",
            overview = "A thief steals corporate secrets through dream-sharing technology.",
            releaseDate = "2010-07-16",
            genres = listOf(Genre(id = 28, name = "Action")),
            spokenLanguages = listOf(SpokenLanguage(name = "English")),
            voteAverage = 8.4,
            voteCount = 37000
        )
        val MovieReviewResponse = MovieReviewResponseDto(
            page = 3,
            totalPages = 5,
            totalResults = 45,
            results = listOf(
                MovieReviewDto(
                    id = "review-1",
                    author = "Papay",
                    content = "Still rules.",
                    createdAt = "2026-06-04T09:00:00Z",
                    authorDetails = AuthorDetailsDto(rating = 9.0)
                )
            )
        )
        val MovieReviewPageModel = MovieReviewPage(
            page = 3,
            totalPages = 5,
            totalResults = 45,
            reviews = listOf(
                MovieReview(
                    id = "review-1",
                    author = "Papay",
                    content = "Still rules.",
                    createdAt = "2026-06-04T09:00:00Z",
                    rating = 9.0
                )
            )
        )
        val MovieVideoResponse = MovieVideoResponseDto(
            results = listOf(MovieVideoDto(key = "abc123", name = "Official Trailer", site = "YouTube", type = "Trailer"))
        )
        val MovieVideos = listOf(
            MovieVideo(key = "abc123", name = "Official Trailer", site = "YouTube", type = "Trailer")
        )
    }
}
