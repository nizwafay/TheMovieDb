package com.papay.themoviedb.feature.movies.detail

import androidx.lifecycle.SavedStateHandle
import com.papay.themoviedb.core.domain.error.AppException
import com.papay.themoviedb.core.domain.repository.MovieRepository
import com.papay.themoviedb.core.domain.usecase.GetMovieDetailUseCase
import com.papay.themoviedb.core.domain.usecase.GetMovieReviewsUseCase
import com.papay.themoviedb.core.domain.usecase.GetMovieUseCase
import com.papay.themoviedb.core.domain.usecase.GetMovieYoutubeTrailerUseCase
import com.papay.themoviedb.core.model.Genre
import com.papay.themoviedb.core.model.Movie
import com.papay.themoviedb.core.model.MovieDetail
import com.papay.themoviedb.core.model.MovieReview
import com.papay.themoviedb.core.model.MovieReviewPage
import com.papay.themoviedb.core.model.MovieVideo
import com.papay.themoviedb.core.model.PaginationDefaults
import com.papay.themoviedb.core.model.SpokenLanguage
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

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val movieRepository = mockk<MovieRepository>()

    @Test
    fun `initial load shows movie detail trailer and first reviews page`() = runTest {
        stubSuccessfulInitialLoad()

        val viewModel = createViewModel()
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(UiLoadState.Idle, uiState.loadState)
        assertEquals(MovieDetail, uiState.detail)
        assertEquals(MovieDetail.title, uiState.title)
        assertEquals(YoutubeTrailer, uiState.trailer)
        assertEquals(ReviewsPage1.reviews, uiState.reviews)
        assertEquals(ReviewsPage1.totalResults, uiState.totalReviews)
        assertEquals(ReviewsPage1.page, uiState.currentReviewPage)
        assertTrue(uiState.canLoadMoreReviews)
        assertNull(uiState.message)
        assertNull(uiState.reviewMessage)
    }

    @Test
    fun `initial detail failure keeps local title and shows error message`() = runTest {
        coEvery { movieRepository.getMovie(MovieId) } returns LocalMovie
        coEvery { movieRepository.getMovieDetail(MovieId) } throws AppException.ServerError()

        val viewModel = createViewModel()
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(UiLoadState.Idle, uiState.loadState)
        assertEquals(LocalMovie.title, uiState.title)
        assertNull(uiState.detail)
        assertEquals(CoreUiR.string.error_load_title, uiState.message?.titleRes)
        assertEquals(CoreUiR.string.error_load_description, uiState.message?.descriptionRes)
    }

    @Test
    fun `reviews failure during initial load still shows movie detail`() = runTest {
        coEvery { movieRepository.getMovie(MovieId) } returns LocalMovie
        coEvery { movieRepository.getMovieDetail(MovieId) } returns MovieDetail
        coEvery { movieRepository.getMovieVideos(MovieId) } returns listOf(YoutubeTrailer)
        coEvery {
            movieRepository.getMovieReviews(MovieId, PaginationDefaults.FirstPage)
        } throws AppException.RateLimited()

        val viewModel = createViewModel()
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(UiLoadState.Idle, uiState.loadState)
        assertEquals(MovieDetail, uiState.detail)
        assertTrue(uiState.reviews.isEmpty())
        assertFalse(uiState.canLoadMoreReviews)
        assertEquals(CoreUiR.string.error_rate_limited_title, uiState.reviewMessage?.titleRes)
        assertEquals(CoreUiR.string.error_rate_limited_description, uiState.reviewMessage?.descriptionRes)
    }

    @Test
    fun `load more reviews appends distinct reviews and updates pagination`() = runTest {
        stubSuccessfulInitialLoad()
        coEvery { movieRepository.getMovieReviews(MovieId, 2) } returns ReviewsPage2

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.loadMoreReviews()
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(ReviewsPage1.reviews + ReviewsPage2.reviews, uiState.reviews)
        assertEquals(ReviewsPage2.page, uiState.currentReviewPage)
        assertEquals(ReviewsPage2.totalResults, uiState.totalReviews)
        assertFalse(uiState.canLoadMoreReviews)
        assertFalse(uiState.isLoadingReviews)
        assertNull(uiState.reviewMessage)
    }

    @Test
    fun `load more reviews failure keeps loaded reviews and shows cached data message`() = runTest {
        stubSuccessfulInitialLoad()
        coEvery { movieRepository.getMovieReviews(MovieId, 2) } throws AppException.NetworkUnavailable(
            cause = RuntimeException()
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.loadMoreReviews()
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(ReviewsPage1.reviews, uiState.reviews)
        assertFalse(uiState.canLoadMoreReviews)
        assertFalse(uiState.isLoadingReviews)
        assertEquals(CoreUiR.string.error_offline_title, uiState.reviewMessage?.titleRes)
        assertEquals(CoreUiR.string.state_showing_cached_description, uiState.reviewMessage?.descriptionRes)
    }

    @Test
    fun `show and hide all reviews toggles review panel state`() = runTest {
        stubSuccessfulInitialLoad()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.showAllReviews()
        assertTrue(viewModel.uiState.value.isShowingAllReviews)

        viewModel.hideAllReviews()
        assertFalse(viewModel.uiState.value.isShowingAllReviews)
    }

    private fun createViewModel(): MovieDetailViewModel {
        return MovieDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("movieId" to MovieId)),
            getMovieDetail = GetMovieDetailUseCase(movieRepository = movieRepository),
            getMovieReviews = GetMovieReviewsUseCase(movieRepository = movieRepository),
            getMovie = GetMovieUseCase(movieRepository = movieRepository),
            getMovieYoutubeTrailer = GetMovieYoutubeTrailerUseCase(movieRepository = movieRepository)
        )
    }

    private fun stubSuccessfulInitialLoad() {
        coEvery { movieRepository.getMovie(MovieId) } returns LocalMovie
        coEvery { movieRepository.getMovieDetail(MovieId) } returns MovieDetail
        coEvery { movieRepository.getMovieVideos(MovieId) } returns listOf(
            MovieVideo(
                key = "featurette",
                name = "Featurette",
                site = "YouTube",
                type = "Featurette"
            ),
            YoutubeTrailer
        )
        coEvery { movieRepository.getMovieReviews(MovieId, PaginationDefaults.FirstPage) } returns ReviewsPage1
    }

    private companion object {
        const val MovieId = 550

        val LocalMovie = Movie(
            id = MovieId,
            title = "Fight Club",
            posterPath = "/poster.jpg",
            releaseDate = "1999-10-15"
        )

        val MovieDetail = MovieDetail(
            id = MovieId,
            title = "Fight Club",
            overview = "An insomniac office worker meets a soap maker.",
            releaseDate = "1999-10-15",
            genres = listOf(Genre(id = 18, name = "Drama")),
            spokenLanguages = listOf(SpokenLanguage(name = "English")),
            voteAverage = 8.4,
            voteCount = 12000
        )

        val YoutubeTrailer = MovieVideo(
            key = "trailer",
            name = "Trailer",
            site = "YouTube",
            type = "Trailer"
        )

        val ReviewsPage1 = MovieReviewPage(
            reviews = listOf(
                MovieReview(
                    id = "review-1",
                    author = "Reviewer One",
                    content = "Sharp and strange.",
                    createdAt = "2026-01-01T00:00:00.000Z",
                    rating = 8.0
                )
            ),
            page = 1,
            totalPages = 2,
            totalResults = 2
        )

        val ReviewsPage2 = MovieReviewPage(
            reviews = listOf(
                MovieReview(
                    id = "review-2",
                    author = "Reviewer Two",
                    content = "Still hits hard.",
                    createdAt = "2026-01-02T00:00:00.000Z",
                    rating = 9.0
                )
            ),
            page = 2,
            totalPages = 2,
            totalResults = 2
        )
    }
}
