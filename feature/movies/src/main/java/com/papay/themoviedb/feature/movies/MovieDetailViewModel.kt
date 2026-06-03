package com.papay.themoviedb.feature.movies

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.papay.themoviedb.core.domain.usecase.GetMovieDetailUseCase
import com.papay.themoviedb.core.domain.usecase.GetMovieUseCase
import com.papay.themoviedb.core.domain.usecase.GetMovieYoutubeTrailerUseCase
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
                    message = null
                )
            }

            runCatching {
                val detail = getMovieDetail(movieId = movieId)
                val trailer = runCatching { getMovieYoutubeTrailer(movieId = movieId) }.getOrNull()
                detail to trailer
            }
                .onSuccess { (detail, trailer) ->
                    _uiState.update {
                        it.copy(
                            loadState = UiLoadState.Idle,
                            title = detail.title,
                            detail = detail,
                            trailer = trailer,
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

    private suspend fun loadMovieTitle() {
        if (_uiState.value.title != null) return

        val movie = getMovie(id = movieId)
        _uiState.update { state ->
            state.copy(title = movie?.title)
        }
    }
}
