package com.papay.themoviedb.feature.movies.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.papay.themoviedb.core.domain.usecase.GetGenreUseCase
import com.papay.themoviedb.core.domain.usecase.GetMoviesByGenreUseCase
import com.papay.themoviedb.core.model.PaginationDefaults
import com.papay.themoviedb.core.ui.UiLoadState
import com.papay.themoviedb.core.ui.emptyUiMessageIf
import com.papay.themoviedb.core.ui.toUiMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieListViewModel(
    savedStateHandle: SavedStateHandle,
    private val getGenre: GetGenreUseCase,
    private val getMoviesByGenre: GetMoviesByGenreUseCase
) : ViewModel() {
    val genreId: Int = checkNotNull(savedStateHandle["genreId"])

    private val _uiState = MutableStateFlow(MovieListUiState())
    val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies() {
        if (!_uiState.value.canStartRefresh()) return

        viewModelScope.launch {
            loadGenreName()

            _uiState.update { state ->
                state.copy(
                    loadState = if (state.movies.isEmpty()) {
                        UiLoadState.InitialLoading
                    } else {
                        UiLoadState.Refreshing
                    },
                    message = null
                )
            }

            runCatching {
                getMoviesByGenre(
                    genreId = genreId,
                    page = PaginationDefaults.FirstPage
                )
            }
                .onSuccess { result ->
                    _uiState.update {
                        val message = result.fallbackError
                            ?.toUiMessage(hasCachedData = result.data.movies.isNotEmpty())
                            ?: emptyUiMessageIf(result.data.movies)
                        it.copy(
                            loadState = UiLoadState.Idle,
                            movies = result.data.movies,
                            currentPage = result.data.page,
                            canLoadMore = result.data.page < result.data.totalPages,
                            message = message
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        val message = throwable.toUiMessage(hasCachedData = it.movies.isNotEmpty())
                        it.copy(
                            loadState = UiLoadState.Idle,
                            message = message
                        )
                    }
                }
        }
    }

    fun loadMoreMovies() {
        val state = _uiState.value
        if (!state.canStartLoadingMore()) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingMore = true,
                    message = null
                )
            }

            val nextPage = state.currentPage + 1
            runCatching {
                getMoviesByGenre(
                    genreId = genreId,
                    page = nextPage
                )
            }
                .onSuccess { result ->
                    _uiState.update {
                        val movies = (it.movies + result.data.movies).distinctBy { movie -> movie.id }
                        it.copy(
                            movies = movies,
                            currentPage = result.data.page,
                            canLoadMore = result.data.page < result.data.totalPages,
                            isLoadingMore = false,
                            message = result.fallbackError?.toUiMessage(hasCachedData = movies.isNotEmpty())
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoadingMore = false,
                            message = throwable.toUiMessage(hasCachedData = it.movies.isNotEmpty())
                        )
                    }
                }
        }
    }

    private suspend fun loadGenreName() {
        if (_uiState.value.genreName != null) return

        val genre = getGenre(id = genreId)
        _uiState.update { state ->
            state.copy(genreName = genre?.name)
        }
    }

    private fun MovieListUiState.canStartRefresh(): Boolean {
        return loadState == UiLoadState.Idle && !isLoadingMore
    }

    private fun MovieListUiState.canStartLoadingMore(): Boolean {
        return loadState == UiLoadState.Idle &&
            !isLoadingMore &&
            canLoadMore &&
            movies.isNotEmpty()
    }

}
