package com.papay.themoviedb.feature.genres

import com.papay.themoviedb.core.model.Genre
import com.papay.themoviedb.core.ui.UiLoadState
import com.papay.themoviedb.core.ui.UiMessage

data class GenreListUiState(
    val loadState: UiLoadState = UiLoadState.Idle,
    val genres: List<Genre> = emptyList(),
    val message: UiMessage? = null
)
