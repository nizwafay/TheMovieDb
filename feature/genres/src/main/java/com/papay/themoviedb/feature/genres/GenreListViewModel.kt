package com.papay.themoviedb.feature.genres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.papay.themoviedb.core.domain.usecase.GetGenresUseCase
import com.papay.themoviedb.core.model.Genre
import com.papay.themoviedb.core.ui.UiLoadState
import com.papay.themoviedb.core.ui.emptyUiMessageIf
import com.papay.themoviedb.core.ui.toUiMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GenreListViewModel(
    private val getGenres: GetGenresUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(GenreListUiState())
    val uiState: StateFlow<GenreListUiState> = _uiState.asStateFlow()

    init {
        loadGenres()
    }

    fun loadGenres() {
        if (_uiState.value.loadState != UiLoadState.Idle) return

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    loadState = if (state.genres.isEmpty()) {
                        UiLoadState.InitialLoading
                    } else {
                        UiLoadState.Refreshing
                    },
                    message = null
                )
            }

            runCatching { getGenres() }
                .onSuccess { result ->
                    _uiState.update {
                        val message = result.fallbackError
                            ?.toUiMessage(hasCachedData = result.data.isNotEmpty())
                            ?: emptyUiMessageIf(result.data)
                        it.copy(
                            loadState = UiLoadState.Idle,
                            genres = result.data,
                            message = message
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        val message = throwable.toUiMessage(hasCachedData = it.genres.isNotEmpty())
                        it.copy(
                            loadState = UiLoadState.Idle,
                            message = message
                        )
                    }
                }
        }
    }
}
