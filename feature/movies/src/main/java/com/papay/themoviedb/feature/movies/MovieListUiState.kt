package com.papay.themoviedb.feature.movies

import com.papay.themoviedb.core.model.Movie
import com.papay.themoviedb.core.ui.UiLoadState
import com.papay.themoviedb.core.ui.UiMessage

data class MovieListUiState(
    val loadState: UiLoadState = UiLoadState.Idle,
    val genreName: String? = null,
    val movies: List<Movie> = emptyList(),
    val currentPage: Int = 0,
    val canLoadMore: Boolean = true,
    val isLoadingMore: Boolean = false,
    val message: UiMessage? = null
)
