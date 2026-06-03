package com.papay.themoviedb.feature.movies.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

@Composable
fun MovieDetailRoute(
    viewModel: MovieDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MovieDetailScreen(
        uiState = uiState,
        onRetryClick = viewModel::loadMovieDetail,
        onLoadMoreReviews = viewModel::loadMoreReviews,
        onRetryReviews = viewModel::retryReviews,
        onShowAllReviews = viewModel::showAllReviews,
        onHideAllReviews = viewModel::hideAllReviews
    )
}
