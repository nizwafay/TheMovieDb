package com.papay.themoviedb.feature.movies.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.papay.themoviedb.core.domain.usecase.GetMovieDetailUseCase
import com.papay.themoviedb.core.domain.usecase.GetMovieReviewsUseCase
import com.papay.themoviedb.core.domain.usecase.GetMovieUseCase
import com.papay.themoviedb.core.domain.usecase.GetMovieYoutubeTrailerUseCase
import com.papay.themoviedb.core.model.MovieDetail
import com.papay.themoviedb.core.model.MovieReviewPage
import com.papay.themoviedb.core.model.MovieVideo
import com.papay.themoviedb.core.ui.UiLoadState
import com.papay.themoviedb.core.ui.toUiMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetail: GetMovieDetailUseCase,
    private val getMovieReviews: GetMovieReviewsUseCase,
    private val getMovie: GetMovieUseCase,
    private val getMovieYoutubeTrailer: GetMovieYoutubeTrailerUseCase
) : ViewModel() {
    private val movieId: Int = checkNotNull(savedStateHandle["movieId"])

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    init {
        loadMovieDetail()
    }

    fun loadMovieDetail() {
        if (_uiState.value.loadState != UiLoadState.Idle) return

        viewModelScope.launch {
            loadMovieTitle()

            _uiState.update {
                it.copy(
                    loadState = UiLoadState.InitialLoading,
                    reviewMessage = null,
                    message = null
                )
            }

            runCatching {
                val detail = getMovieDetail(movieId = movieId)
                val trailer = runCatching { getMovieYoutubeTrailer(movieId = movieId) }.getOrNull()
                val reviewsResult = runCatching {
                    getMovieReviews(movieId = movieId, page = FirstPage)
                }
                MovieDetailResult(
                    detail = detail,
                    trailer = trailer,
                    reviewsPage = reviewsResult.getOrNull(),
                    reviewsError = reviewsResult.exceptionOrNull()
                )
            }
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            loadState = UiLoadState.Idle,
                            title = result.detail.title,
                            detail = result.detail,
                            trailer = result.trailer,
                            reviews = result.reviewsPage?.reviews.orEmpty(),
                            totalReviews = result.reviewsPage?.totalResults ?: 0,
                            currentReviewPage = result.reviewsPage?.page ?: 0,
                            canLoadMoreReviews = result.reviewsPage
                                ?.let { page -> page.page < page.totalPages }
                                ?: false,
                            isLoadingReviews = false,
                            reviewMessage = result.reviewsError?.toUiMessage(hasCachedData = false),
                            message = null
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            loadState = UiLoadState.Idle,
                            message = throwable.toUiMessage(hasCachedData = false)
                        )
                    }
                }
        }
    }

    fun loadMoreReviews() {
        loadReviewsPage(ignoreLoadMoreLimit = false)
    }

    fun retryReviews() {
        loadReviewsPage(ignoreLoadMoreLimit = true)
    }

    fun showAllReviews() {
        _uiState.update {
            it.copy(isShowingAllReviews = true)
        }
    }

    fun hideAllReviews() {
        _uiState.update {
            it.copy(isShowingAllReviews = false)
        }
    }

    private fun loadReviewsPage(ignoreLoadMoreLimit: Boolean) {
        val state = _uiState.value
        if (!state.canStartLoadingMoreReviews(ignoreLoadMoreLimit)) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingReviews = true,
                    reviewMessage = null
                )
            }

            val nextPage = state.currentReviewPage + 1
            runCatching {
                getMovieReviews(
                    movieId = movieId,
                    page = nextPage
                )
            }
                .onSuccess { page ->
                    _uiState.update {
                        val reviews = (it.reviews + page.reviews).distinctBy { review -> review.id }
                        it.copy(
                            reviews = reviews,
                            totalReviews = page.totalResults,
                            currentReviewPage = page.page,
                            canLoadMoreReviews = page.page < page.totalPages,
                            isLoadingReviews = false,
                            reviewMessage = null
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoadingReviews = false,
                            canLoadMoreReviews = false,
                            reviewMessage = throwable.toUiMessage(hasCachedData = it.reviews.isNotEmpty())
                        )
                    }
                }
        }
    }

    private suspend fun loadMovieTitle() {
        if (_uiState.value.title != null) return

        val movie = getMovie(id = movieId)
        _uiState.update { state ->
            state.copy(title = movie?.title)
        }
    }

    private fun MovieDetailUiState.canStartLoadingMoreReviews(ignoreLoadMoreLimit: Boolean): Boolean {
        return loadState == UiLoadState.Idle &&
            !isLoadingReviews &&
            (canLoadMoreReviews || ignoreLoadMoreLimit) &&
            detail != null
    }

    private data class MovieDetailResult(
        val detail: MovieDetail,
        val trailer: MovieVideo?,
        val reviewsPage: MovieReviewPage?,
        val reviewsError: Throwable?
    )

    private companion object {
        const val FirstPage = 1
    }
}
