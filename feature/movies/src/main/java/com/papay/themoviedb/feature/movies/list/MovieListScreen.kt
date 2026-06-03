package com.papay.themoviedb.feature.movies.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.papay.themoviedb.core.model.Movie
import com.papay.themoviedb.core.ui.LoadingContent
import com.papay.themoviedb.core.ui.RefreshableContent
import com.papay.themoviedb.core.ui.RefreshingCard
import com.papay.themoviedb.core.ui.UiLoadState
import com.papay.themoviedb.core.ui.UiMessageContent
import com.papay.themoviedb.core.ui.rememberUiMessageSnackbarHostState
import com.papay.themoviedb.feature.movies.R
import com.papay.themoviedb.core.ui.R as CoreUiR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    uiState: MovieListUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onLoadMore: () -> Unit,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = rememberUiMessageSnackbarHostState(
        message = uiState.message,
        showMessage = uiState.movies.isNotEmpty(),
        actionLabel = stringResource(CoreUiR.string.action_retry),
        onAction = onRetryClick
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MovieListTopAppBar(
                genreName = uiState.genreName ?: stringResource(R.string.movie_list_unknown_genre),
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        when (uiState.loadState) {
            UiLoadState.InitialLoading -> LoadingContent(
                messageRes = R.string.movie_list_loading,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )

            else -> if (uiState.movies.isEmpty() && uiState.message != null) {
                UiMessageContent(
                    message = uiState.message,
                    onActionClick = onRetryClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            } else {
                MovieListContent(
                    movies = uiState.movies,
                    isRefreshing = uiState.loadState == UiLoadState.Refreshing,
                    isLoadingMore = uiState.isLoadingMore,
                    canLoadMore = uiState.canLoadMore,
                    onRefresh = onRetryClick,
                    onLoadMore = onLoadMore,
                    onMovieClick = onMovieClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MovieListTopAppBar(
    genreName: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = stringResource(R.string.movie_list_title, genreName),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.movie_list_back)
                )
            }
        }
    )
}

@Composable
private fun MovieListContent(
    movies: List<Movie>,
    isRefreshing: Boolean,
    isLoadingMore: Boolean,
    canLoadMore: Boolean,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyGridState()
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = gridState.layoutInfo.totalItemsCount
            totalItems > 0 && lastVisibleIndex >= totalItems - LoadMoreThreshold
        }
    }

    LaunchedEffect(movies.size, canLoadMore, isLoadingMore) {
        snapshotFlow { shouldLoadMore.value }
            .collect { shouldLoad ->
                if (shouldLoad && canLoadMore && !isLoadingMore) {
                    onLoadMore()
                }
            }
    }

    RefreshableContent(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 148.dp),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isRefreshing) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    RefreshingCard(messageRes = R.string.movie_list_refreshing)
                }
            }

            items(
                items = movies,
                key = { movie -> movie.id }
            ) { movie ->
                MovieCard(
                    movie = movie,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onMovieClick(movie) }
                )
            }

            if (isLoadingMore) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieCard(
    movie: Movie,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MoviePoster(
                movie = movie,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(PosterAspectRatio)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = movie.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = movie.releaseYear() ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun MoviePoster(
    movie: Movie,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(6.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = movie.posterUrl(),
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (movie.posterPath == null) {
            Text(
                text = movie.title.firstOrNull()?.uppercase() ?: "",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun Movie.posterUrl(): String? {
    return posterPath?.let { path -> "https://image.tmdb.org/t/p/w342$path" }
}

private fun Movie.releaseYear(): String? {
    return releaseDate?.takeIf { date -> date.length >= 4 }?.take(4)
}

@Preview(showBackground = true)
@Composable
private fun MovieListScreenPreview() {
    MaterialTheme {
        MovieListScreen(
            uiState = MovieListUiState(
                genreName = "Action",
                movies = listOf(
                    Movie(
                        id = 1,
                        title = "Example Movie",
                        posterPath = null,
                        releaseDate = "2026-01-01"
                    )
                )
            ),
            onBackClick = {},
            onRetryClick = {},
            onLoadMore = {},
            onMovieClick = {}
        )
    }
}

private const val LoadMoreThreshold = 6
private const val PosterAspectRatio = 2f / 3f
