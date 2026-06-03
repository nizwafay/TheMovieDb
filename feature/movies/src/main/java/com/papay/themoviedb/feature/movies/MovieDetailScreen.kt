package com.papay.themoviedb.feature.movies

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.papay.themoviedb.core.model.MovieVideo
import com.papay.themoviedb.core.ui.LoadingContent
import com.papay.themoviedb.core.ui.UiLoadState
import com.papay.themoviedb.core.ui.UiMessageContent
import com.papay.themoviedb.core.ui.YouTubePlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    uiState: MovieDetailUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = uiState.title ?: stringResource(R.string.movie_detail_title))
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text(text = "<")
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            uiState.loadState == UiLoadState.InitialLoading -> LoadingContent(
                messageRes = R.string.movie_detail_loading,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )

            uiState.trailer != null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                YouTubePlayer(
                    videoKey = uiState.trailer.key,
                    title = uiState.trailer.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(VideoAspectRatio)
                )
            }

            uiState.message != null -> UiMessageContent(
                message = uiState.message,
                onActionClick = onRetryClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

private const val VideoAspectRatio = 16f / 9f

@Preview(showBackground = true)
@Composable
private fun MovieDetailScreenPreview() {
    MaterialTheme {
        MovieDetailScreen(
            uiState = MovieDetailUiState(
                title = "Example Movie",
                trailer = MovieVideo(
                    key = "example",
                    name = "Trailer",
                    site = "YouTube",
                    type = "Trailer"
                )
            ),
            onBackClick = {},
            onRetryClick = {}
        )
    }
}
