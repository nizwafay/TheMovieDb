package com.papay.themoviedb.feature.genres

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

@Composable
fun GenreListRoute(
    viewModel: GenreListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    GenreListScreen(
        uiState = uiState,
        onRetryClick = viewModel::loadGenres
    )
}
