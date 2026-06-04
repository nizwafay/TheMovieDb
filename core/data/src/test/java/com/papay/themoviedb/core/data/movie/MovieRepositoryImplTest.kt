package com.papay.themoviedb.core.data.movie

import com.papay.themoviedb.core.data.movie.datasource.MovieLocalDataSource
import com.papay.themoviedb.core.data.movie.datasource.MovieRemoteDataSource
import com.papay.themoviedb.core.data.testing.TestDispatcherProvider
import com.papay.themoviedb.core.data.testing.assertThrows
import com.papay.themoviedb.core.model.Movie
import com.papay.themoviedb.core.model.MovieDetail
import com.papay.themoviedb.core.model.MoviePage
import com.papay.themoviedb.core.model.MovieReviewPage
import com.papay.themoviedb.core.model.MovieVideo
import com.papay.themoviedb.core.model.PaginationDefaults
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class MovieRepositoryImplTest {
    private val movieLocalDataSource = mockk<MovieLocalDataSource>()
    private val movieRemoteDataSource = mockk<MovieRemoteDataSource>()
    private val testDispatcher = StandardTestDispatcher()
    private val repository = MovieRepositoryImpl(
        movieLocalDataSource = movieLocalDataSource,
        movieRemoteDataSource = movieRemoteDataSource,
        dispatcherProvider = TestDispatcherProvider(testDispatcher)
    )

    @Test
    fun `get movie returns local movie`() = runTest(testDispatcher) {
        coEvery { movieLocalDataSource.getMovie(id = 1) } returns Movies.first()

        val result = repository.getMovie(id = 1)

        assertEquals(Movies.first(), result)
        coVerify { movieLocalDataSource.getMovie(id = 1) }
    }

    @Test
    fun `get movie detail returns remote movie detail`() = runTest(testDispatcher) {
        coEvery { movieRemoteDataSource.getMovieDetail(movieId = 1) } returns MovieDetailModel

        val result = repository.getMovieDetail(movieId = 1)

        assertEquals(MovieDetailModel, result)
        coVerify { movieRemoteDataSource.getMovieDetail(movieId = 1) }
    }

    @Test
    fun `get movie reviews returns remote movie reviews`() = runTest(testDispatcher) {
        coEvery { movieRemoteDataSource.getMovieReviews(movieId = 1, page = 2) } returns MovieReviewPageModel

        val result = repository.getMovieReviews(movieId = 1, page = 2)

        assertEquals(MovieReviewPageModel, result)
        coVerify { movieRemoteDataSource.getMovieReviews(movieId = 1, page = 2) }
    }

    @Test
    fun `get movie videos returns remote movie videos`() = runTest(testDispatcher) {
        coEvery { movieRemoteDataSource.getMovieVideos(movieId = 1) } returns MovieVideos

        val result = repository.getMovieVideos(movieId = 1)

        assertEquals(MovieVideos, result)
        coVerify { movieRemoteDataSource.getMovieVideos(movieId = 1) }
    }

    @Test
    fun `get movies by genre saves remote movies and returns cached first page`() = runTest(testDispatcher) {
        coEvery {
            movieRemoteDataSource.getMoviesByGenre(genreId = 28, page = PaginationDefaults.FirstPage)
        } returns RemoteMoviePage
        coEvery { movieLocalDataSource.upsertMovies(genreId = 28, movies = RemoteMoviePage.movies) } returns Unit
        coEvery { movieLocalDataSource.getMoviesByGenre(genreId = 28) } returns CachedMovies

        val result = repository.getMoviesByGenre(genreId = 28, page = PaginationDefaults.FirstPage)

        assertEquals(
            MoviePage(
                movies = CachedMovies,
                page = PaginationDefaults.FirstPage,
                totalPages = RemoteMoviePage.totalPages
            ),
            result.data
        )
        assertNull(result.fallbackError)
        coVerifyOrder {
            movieRemoteDataSource.getMoviesByGenre(genreId = 28, page = PaginationDefaults.FirstPage)
            movieLocalDataSource.upsertMovies(genreId = 28, movies = RemoteMoviePage.movies)
            movieLocalDataSource.getMoviesByGenre(genreId = 28)
        }
    }

    @Test
    fun `get movies by genre returns remote page when first page cache is empty`() = runTest(testDispatcher) {
        coEvery {
            movieRemoteDataSource.getMoviesByGenre(genreId = 28, page = PaginationDefaults.FirstPage)
        } returns RemoteMoviePage
        coEvery { movieLocalDataSource.upsertMovies(genreId = 28, movies = RemoteMoviePage.movies) } returns Unit
        coEvery { movieLocalDataSource.getMoviesByGenre(genreId = 28) } returns emptyList()

        val result = repository.getMoviesByGenre(genreId = 28, page = PaginationDefaults.FirstPage)

        assertEquals(RemoteMoviePage, result.data)
        assertNull(result.fallbackError)
    }

    @Test
    fun `get movies by genre returns remote page and does not read cache after first page`() = runTest(testDispatcher) {
        coEvery { movieRemoteDataSource.getMoviesByGenre(genreId = 28, page = 2) } returns RemoteSecondMoviePage
        coEvery { movieLocalDataSource.upsertMovies(genreId = 28, movies = RemoteSecondMoviePage.movies) } returns Unit

        val result = repository.getMoviesByGenre(genreId = 28, page = 2)

        assertEquals(RemoteSecondMoviePage, result.data)
        assertNull(result.fallbackError)
        coVerify(exactly = 0) { movieLocalDataSource.getMoviesByGenre(any()) }
    }

    @Test
    fun `get movies by genre returns first page cache and fallback error when remote fails`() = runTest(testDispatcher) {
        val remoteError = RuntimeException("Remote failed")
        coEvery {
            movieRemoteDataSource.getMoviesByGenre(genreId = 28, page = PaginationDefaults.FirstPage)
        } throws remoteError
        coEvery { movieLocalDataSource.getMoviesByGenre(genreId = 28) } returns CachedMovies

        val result = repository.getMoviesByGenre(genreId = 28, page = PaginationDefaults.FirstPage)

        assertEquals(
            MoviePage(
                movies = CachedMovies,
                page = PaginationDefaults.FirstPage,
                totalPages = PaginationDefaults.FirstPage
            ),
            result.data
        )
        assertSame(remoteError, result.fallbackError)
        coVerify(exactly = 0) { movieLocalDataSource.upsertMovies(any(), any()) }
    }

    @Test
    fun `get movies by genre throws remote error when later page remote fails`() = runTest(testDispatcher) {
        val remoteError = RuntimeException("Remote failed")
        coEvery { movieRemoteDataSource.getMoviesByGenre(genreId = 28, page = 2) } throws remoteError

        val error = assertThrows<RuntimeException> {
            repository.getMoviesByGenre(genreId = 28, page = 2)
        }

        assertEquals(remoteError.message, error.message)
        coVerify(exactly = 0) { movieLocalDataSource.getMoviesByGenre(any()) }
        coVerify(exactly = 0) { movieLocalDataSource.upsertMovies(any(), any()) }
    }

    private companion object {
        val Movies = listOf(
            Movie(id = 1, title = "Inception", posterPath = "/inception.jpg", releaseDate = "2010-07-16")
        )
        val CachedMovies = listOf(
            Movie(id = 99, title = "Cached Movie", posterPath = "/cached.jpg", releaseDate = "2020-01-01")
        )
        val RemoteMoviePage = MoviePage(
            movies = Movies,
            page = PaginationDefaults.FirstPage,
            totalPages = 10
        )
        val RemoteSecondMoviePage = MoviePage(
            movies = listOf(Movie(id = 2, title = "Interstellar", posterPath = "/interstellar.jpg", releaseDate = "2014-11-07")),
            page = 2,
            totalPages = 10
        )
        val MovieDetailModel = MovieDetail(
            id = 1,
            title = "Inception",
            overview = "A thief steals corporate secrets through dream-sharing technology.",
            releaseDate = "2010-07-16",
            genres = emptyList(),
            spokenLanguages = emptyList(),
            voteAverage = 8.4,
            voteCount = 37000
        )
        val MovieReviewPageModel = MovieReviewPage(
            reviews = emptyList(),
            page = 2,
            totalPages = 5,
            totalResults = 40
        )
        val MovieVideos = listOf(
            MovieVideo(key = "abc123", name = "Official Trailer", site = "YouTube", type = "Trailer")
        )
    }
}
