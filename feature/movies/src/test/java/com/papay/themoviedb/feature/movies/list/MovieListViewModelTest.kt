package com.papay.themoviedb.feature.movies.list

import androidx.lifecycle.SavedStateHandle
import com.papay.themoviedb.core.domain.error.AppException
import com.papay.themoviedb.core.domain.repository.GenreRepository
import com.papay.themoviedb.core.domain.repository.MovieRepository
import com.papay.themoviedb.core.domain.result.DataResult
import com.papay.themoviedb.core.domain.usecase.GetGenreUseCase
import com.papay.themoviedb.core.domain.usecase.GetMoviesByGenreUseCase
import com.papay.themoviedb.core.model.Genre
import com.papay.themoviedb.core.model.Movie
import com.papay.themoviedb.core.model.MoviePage
import com.papay.themoviedb.core.model.PaginationDefaults
import com.papay.themoviedb.core.testing.MainDispatcherRule
import com.papay.themoviedb.core.ui.R as CoreUiR
import com.papay.themoviedb.core.ui.UiLoadState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class MovieListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val genreRepository = mockk<GenreRepository>()
    private val movieRepository = mockk<MovieRepository>()

    @Test
    fun `initial load shows genre name and movies when request succeeds`() = runTest {
        stubGenre()
        coEvery {
            movieRepository.getMoviesByGenre(GenreId, PaginationDefaults.FirstPage)
        } returns DataResult(data = MoviesPage1)

        val viewModel = createViewModel()
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(UiLoadState.Idle, uiState.loadState)
        assertEquals(Genre.name, uiState.genreName)
        assertEquals(MoviesPage1.movies, uiState.movies)
        assertEquals(MoviesPage1.page, uiState.currentPage)
        assertTrue(uiState.canLoadMore)
        assertFalse(uiState.isLoadingMore)
        assertNull(uiState.message)
    }

    @Test
    fun `initial load shows empty message when request returns no movies`() = runTest {
        stubGenre()
        coEvery {
            movieRepository.getMoviesByGenre(GenreId, PaginationDefaults.FirstPage)
        } returns DataResult(
            data = MoviePage(
                movies = emptyList(),
                page = PaginationDefaults.FirstPage,
                totalPages = PaginationDefaults.FirstPage
            )
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(UiLoadState.Idle, uiState.loadState)
        assertEquals(Genre.name, uiState.genreName)
        assertTrue(uiState.movies.isEmpty())
        assertFalse(uiState.canLoadMore)
        assertEquals(CoreUiR.string.state_empty_title, uiState.message?.titleRes)
        assertEquals(CoreUiR.string.state_empty_description, uiState.message?.descriptionRes)
    }

    @Test
    fun `initial load failure shows error message`() = runTest {
        stubGenre()
        coEvery {
            movieRepository.getMoviesByGenre(GenreId, PaginationDefaults.FirstPage)
        } throws AppException.NetworkUnavailable(IOException())

        val viewModel = createViewModel()
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(UiLoadState.Idle, uiState.loadState)
        assertEquals(Genre.name, uiState.genreName)
        assertTrue(uiState.movies.isEmpty())
        assertEquals(CoreUiR.string.error_offline_title, uiState.message?.titleRes)
        assertEquals(CoreUiR.string.error_offline_description, uiState.message?.descriptionRes)
    }

    @Test
    fun `fallback error with movies shows movies and cached data message`() = runTest {
        stubGenre()
        coEvery {
            movieRepository.getMoviesByGenre(GenreId, PaginationDefaults.FirstPage)
        } returns DataResult(
            data = MoviesPage1,
            fallbackError = AppException.RateLimited()
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(UiLoadState.Idle, uiState.loadState)
        assertEquals(MoviesPage1.movies, uiState.movies)
        assertEquals(CoreUiR.string.error_rate_limited_title, uiState.message?.titleRes)
        assertEquals(CoreUiR.string.state_showing_cached_description, uiState.message?.descriptionRes)
    }

    @Test
    fun `load more movies appends distinct movies and updates pagination`() = runTest {
        stubSuccessfulInitialLoad()
        coEvery { movieRepository.getMoviesByGenre(GenreId, 2) } returns DataResult(data = MoviesPage2)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.loadMoreMovies()
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(listOf(Movie1, Movie2, Movie3), uiState.movies)
        assertEquals(MoviesPage2.page, uiState.currentPage)
        assertFalse(uiState.canLoadMore)
        assertFalse(uiState.isLoadingMore)
        assertNull(uiState.message)
    }

    @Test
    fun `load more failure keeps loaded movies and shows cached data message`() = runTest {
        stubSuccessfulInitialLoad()
        coEvery { movieRepository.getMoviesByGenre(GenreId, 2) } throws AppException.ServerError()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.loadMoreMovies()
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(MoviesPage1.movies, uiState.movies)
        assertEquals(MoviesPage1.page, uiState.currentPage)
        assertTrue(uiState.canLoadMore)
        assertFalse(uiState.isLoadingMore)
        assertEquals(CoreUiR.string.error_load_title, uiState.message?.titleRes)
        assertEquals(CoreUiR.string.state_showing_cached_description, uiState.message?.descriptionRes)
    }

    private fun createViewModel(): MovieListViewModel {
        return MovieListViewModel(
            savedStateHandle = SavedStateHandle(mapOf("genreId" to GenreId)),
            getGenre = GetGenreUseCase(genreRepository = genreRepository),
            getMoviesByGenre = GetMoviesByGenreUseCase(movieRepository = movieRepository)
        )
    }

    private fun stubSuccessfulInitialLoad() {
        stubGenre()
        coEvery {
            movieRepository.getMoviesByGenre(GenreId, PaginationDefaults.FirstPage)
        } returns DataResult(data = MoviesPage1)
    }

    private fun stubGenre() {
        coEvery { genreRepository.getGenre(GenreId) } returns Genre
    }

    private companion object {
        const val GenreId = 28

        val Genre = Genre(id = GenreId, name = "Action")

        val Movie1 = Movie(
            id = 1,
            title = "First Movie",
            posterPath = "/first.jpg",
            releaseDate = "2026-01-01"
        )

        val Movie2 = Movie(
            id = 2,
            title = "Second Movie",
            posterPath = "/second.jpg",
            releaseDate = "2026-02-01"
        )

        val Movie3 = Movie(
            id = 3,
            title = "Third Movie",
            posterPath = "/third.jpg",
            releaseDate = "2026-03-01"
        )

        val MoviesPage1 = MoviePage(
            movies = listOf(Movie1, Movie2),
            page = PaginationDefaults.FirstPage,
            totalPages = 2
        )

        val MoviesPage2 = MoviePage(
            movies = listOf(Movie2, Movie3),
            page = 2,
            totalPages = 2
        )
    }
}
