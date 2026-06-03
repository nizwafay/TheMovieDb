package com.papay.themoviedb.feature.movies.detail

import com.papay.themoviedb.core.model.MovieDetail
import com.papay.themoviedb.core.model.MovieReview
import com.papay.themoviedb.core.model.MovieVideo
import com.papay.themoviedb.core.ui.UiLoadState
import com.papay.themoviedb.core.ui.UiMessage

data class MovieDetailUiState(
    val loadState: UiLoadState = UiLoadState.Idle,
    val title: String? = null,
    val detail: MovieDetail? = null,
    val trailer: MovieVideo? = null,
    val reviews: List<MovieReview> = emptyList(),
    val totalReviews: Int = 0,
    val currentReviewPage: Int = 0,
    val canLoadMoreReviews: Boolean = true,
    val isLoadingReviews: Boolean = false,
    val isShowingAllReviews: Boolean = false,
    val reviewMessage: UiMessage? = null,
    val message: UiMessage? = null
)
