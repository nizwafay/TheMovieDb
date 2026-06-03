package com.papay.themoviedb.feature.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
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
import androidx.compose.ui.unit.dp
import com.papay.themoviedb.core.model.Genre
import com.papay.themoviedb.core.model.MovieDetail
import com.papay.themoviedb.core.model.MovieVideo
import com.papay.themoviedb.core.model.SpokenLanguage
import com.papay.themoviedb.core.ui.DateFormatter
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

            uiState.detail != null -> MovieDetailContent(
                detail = uiState.detail,
                trailer = uiState.trailer,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )

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

@Composable
private fun MovieDetailContent(
    detail: MovieDetail,
    trailer: MovieVideo?,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        trailer?.let { movieTrailer ->
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    YouTubePlayer(
                        videoKey = movieTrailer.key,
                        title = movieTrailer.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(VideoAspectRatio)
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = detail.title,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = detail.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                DetailText(
                    label = stringResource(R.string.movie_detail_rating_label),
                    value = stringResource(
                        R.string.movie_detail_rating_value,
                        detail.voteAverage,
                        detail.voteCount
                    )
                )
                DetailText(
                    label = stringResource(R.string.movie_detail_release_date_label),
                    value = DateFormatter.formatDate(detail.releaseDate)
                        ?: stringResource(R.string.movie_detail_unknown_value)
                )
                ChipSection(
                    title = stringResource(R.string.movie_detail_genres_label),
                    labels = detail.genres.map { genre -> genre.name }
                )
                ChipSection(
                    title = stringResource(R.string.movie_detail_spoken_languages_label),
                    labels = detail.spokenLanguages.map { language -> language.name }
                )
            }
        }
    }
}

@Composable
private fun DetailText(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipSection(
    title: String,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            labels.filter { label -> label.isNotBlank() }.forEach { label ->
                AssistChip(
                    onClick = {},
                    label = { Text(text = label) }
                )
            }
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
                detail = MovieDetail(
                    id = 1,
                    title = "Example Movie",
                    overview = "A compact movie overview for previewing the detail layout.",
                    releaseDate = "2026-01-01",
                    genres = listOf(
                        Genre(id = 28, name = "Action"),
                        Genre(id = 12, name = "Adventure")
                    ),
                    spokenLanguages = listOf(
                        SpokenLanguage(name = "English"),
                        SpokenLanguage(name = "German")
                    ),
                    voteAverage = 7.4,
                    voteCount = 1200
                ),
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
