package com.papay.themoviedb.feature.movies

import com.papay.themoviedb.core.model.MovieDetail
import com.papay.themoviedb.core.model.MovieVideo
import com.papay.themoviedb.core.ui.UiLoadState
import com.papay.themoviedb.core.ui.UiMessage

data class MovieDetailUiState(
    val loadState: UiLoadState = UiLoadState.Idle,
    val title: String? = null,
    val detail: MovieDetail? = null,
    val trailer: MovieVideo? = null,
    val message: UiMessage? = null
)
