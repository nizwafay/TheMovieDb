package com.papay.themoviedb.feature.genres

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.papay.themoviedb.core.model.Genre
import org.koin.androidx.compose.koinViewModel

@Composable
fun GenreListRoute(
    onGenreClick: (Genre) -> Unit,
    viewModel: GenreListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    GenreListScreen(
        uiState = uiState,
        onGenreClick = onGenreClick,
        onRetryClick = viewModel::loadGenres
    )
}
