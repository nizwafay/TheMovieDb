package com.papay.themoviedb.feature.genres

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.papay.themoviedb.core.model.Genre
import com.papay.themoviedb.core.ui.LoadingContent
import com.papay.themoviedb.core.ui.RefreshableContent
import com.papay.themoviedb.core.ui.RefreshingCard
import com.papay.themoviedb.core.ui.UiLoadState
import com.papay.themoviedb.core.ui.UiMessageContent
import com.papay.themoviedb.core.ui.rememberUiMessageSnackbarHostState
import com.papay.themoviedb.core.ui.R as CoreUiR

@Composable
fun GenreListScreen(
    uiState: GenreListUiState,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = rememberUiMessageSnackbarHostState(
        message = uiState.message,
        showMessage = uiState.genres.isNotEmpty(),
        actionLabel = stringResource(CoreUiR.string.action_retry),
        onAction = onRetryClick
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        when (uiState.loadState) {
            UiLoadState.InitialLoading -> LoadingContent(
                messageRes = R.string.genre_list_loading,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )

            else -> if (uiState.genres.isEmpty() && uiState.message != null) {
                UiMessageContent(
                    message = uiState.message,
                    onActionClick = onRetryClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            } else {
                GenreListContent(
                    genres = uiState.genres,
                    isRefreshing = uiState.loadState == UiLoadState.Refreshing,
                    onRefresh = onRetryClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun GenreListContent(
    genres: List<Genre>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    RefreshableContent(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isRefreshing) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    RefreshingCard(messageRes = R.string.genre_list_refreshing)
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    modifier = Modifier.padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.genre_list_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(R.string.genre_list_categories_count, genres.size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            itemsIndexed(
                items = genres,
                key = { _, genre -> genre.id }
            ) { index, genre ->
                GenreCard(
                    genre = genre,
                    colors = genreCardColors(index)
                )
            }
        }
    }
}

@Composable
private fun GenreCard(
    genre: Genre,
    colors: GenreCardColors,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.18f),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.container,
            contentColor = colors.content
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = genre.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun genreCardColors(index: Int): GenreCardColors {
    val colorScheme = MaterialTheme.colorScheme
    val colors = listOf(
        GenreCardColors(
            container = colorScheme.primaryContainer,
            content = colorScheme.onPrimaryContainer
        ),
        GenreCardColors(
            container = colorScheme.secondaryContainer,
            content = colorScheme.onSecondaryContainer
        ),
        GenreCardColors(
            container = colorScheme.tertiaryContainer,
            content = colorScheme.onTertiaryContainer
        ),
        GenreCardColors(
            container = colorScheme.surfaceContainerHigh,
            content = colorScheme.onSurface
        )
    )
    return colors[index % colors.size]
}

private data class GenreCardColors(
    val container: Color,
    val content: Color
)

@Preview(showBackground = true)
@Composable
private fun GenreListScreenPreview() {
    MaterialTheme {
        GenreListScreen(
            uiState = GenreListUiState(
                genres = listOf(
                    Genre(id = 28, name = "Action"),
                    Genre(id = 878, name = "Science Fiction"),
                    Genre(id = 9648, name = "Mystery")
                )
            ),
            onRetryClick = {}
        )
    }
}
