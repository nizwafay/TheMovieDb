package com.papay.themoviedb.feature.movies.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.papay.themoviedb.core.model.Movie
import org.koin.androidx.compose.koinViewModel

@Composable
fun MovieListRoute(
    onBackClick: () -> Unit,
    onMovieClick: (Movie) -> Unit,
    viewModel: MovieListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MovieListScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onRetryClick = viewModel::loadMovies,
        onLoadMore = viewModel::loadMoreMovies,
        onMovieClick = onMovieClick
    )
}
