package com.papay.themoviedb.feature.movies.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.papay.themoviedb.core.model.Genre
import com.papay.themoviedb.core.model.MovieDetail
import com.papay.themoviedb.core.model.MovieReview
import com.papay.themoviedb.core.model.MovieVideo
import com.papay.themoviedb.core.model.SpokenLanguage
import com.papay.themoviedb.core.ui.DateFormatter
import com.papay.themoviedb.core.ui.LoadingContent
import com.papay.themoviedb.core.ui.UiLoadState
import com.papay.themoviedb.core.ui.UiMessage
import com.papay.themoviedb.core.ui.UiMessageContent
import com.papay.themoviedb.core.ui.YouTubePlayer
import com.papay.themoviedb.feature.movies.R
import com.papay.themoviedb.feature.movies.reviews.CommentsPanelHeader
import com.papay.themoviedb.feature.movies.reviews.ReviewListItems
import com.papay.themoviedb.feature.movies.reviews.ReviewPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    uiState: MovieDetailUiState,
    onRetryClick: () -> Unit,
    onLoadMoreReviews: () -> Unit,
    onRetryReviews: () -> Unit,
    onShowAllReviews: () -> Unit,
    onHideAllReviews: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
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
                reviews = uiState.reviews,
                totalReviews = uiState.totalReviews,
                isLoadingReviews = uiState.isLoadingReviews,
                canLoadMoreReviews = uiState.canLoadMoreReviews,
                isShowingAllReviews = uiState.isShowingAllReviews,
                reviewMessage = uiState.reviewMessage,
                onLoadMoreReviews = onLoadMoreReviews,
                onRetryReviews = onRetryReviews,
                onShowAllReviews = onShowAllReviews,
                onHideAllReviews = onHideAllReviews,
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
    reviews: List<MovieReview>,
    totalReviews: Int,
    isLoadingReviews: Boolean,
    canLoadMoreReviews: Boolean,
    isShowingAllReviews: Boolean,
    reviewMessage: UiMessage?,
    onLoadMoreReviews: () -> Unit,
    onRetryReviews: () -> Unit,
    onShowAllReviews: () -> Unit,
    onHideAllReviews: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val shouldLoadMoreReviews = remember {
        derivedStateOf {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            totalItems > 0 && lastVisibleIndex >= totalItems - LoadMoreThreshold
        }
    }

    LaunchedEffect(reviews.size, canLoadMoreReviews, isLoadingReviews, isShowingAllReviews) {
        snapshotFlow { shouldLoadMoreReviews.value }
            .collect { shouldLoad ->
                if (isShowingAllReviews && shouldLoad && canLoadMoreReviews && !isLoadingReviews) {
                    onLoadMoreReviews()
                }
            }
    }

    LaunchedEffect(isShowingAllReviews) {
        if (isShowingAllReviews) {
            listState.scrollToItem(0)
        }
    }

    Column(modifier = modifier) {
        trailer?.let { movieTrailer ->
            TrailerPlayer(movieTrailer = movieTrailer)
        }

        AnimatedContent(
            targetState = isShowingAllReviews,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            transitionSpec = {
                if (targetState) {
                    slideInVertically { height -> height } + fadeIn() togetherWith
                        slideOutVertically { height -> -height / 3 } + fadeOut()
                } else {
                    slideInVertically { height -> -height / 3 } + fadeIn() togetherWith
                        slideOutVertically { height -> height } + fadeOut()
                }
            },
            label = "MovieDetailReviewsTransition"
        ) { showingAllReviews ->
            if (showingAllReviews) {
                Column(modifier = Modifier.fillMaxSize()) {
                    CommentsPanelHeader(
                        totalReviews = totalReviews,
                        onCloseClick = onHideAllReviews,
                        modifier = Modifier.fillMaxWidth()
                    )

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(
                            top = 24.dp,
                            bottom = 24.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        ReviewListItems(
                            reviews = reviews,
                            isLoadingReviews = isLoadingReviews,
                            reviewMessage = reviewMessage,
                            onRetryReviews = onRetryReviews
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 24.dp,
                        bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        MovieInfo(detail = detail)
                    }

                    item {
                        ReviewPreview(
                            review = reviews.firstOrNull(),
                            totalReviews = totalReviews,
                            isLoadingReviews = isLoadingReviews,
                            reviewMessage = reviewMessage,
                            onClick = onShowAllReviews,
                            onRetryClick = onRetryReviews,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrailerPlayer(
    movieTrailer: MovieVideo,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
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

@Composable
private fun MovieInfo(
    detail: MovieDetail,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
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
private const val LoadMoreThreshold = 4

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
                ),
                reviews = listOf(
                    MovieReview(
                        id = "review-1",
                        author = "MovieFan",
                        content = "This one surprised me. The pacing is strong and the character moments land better than expected.",
                        createdAt = "2026-01-02T10:30:00.000Z",
                        rating = 8.0
                    )
                )
            ),
            onRetryClick = {},
            onLoadMoreReviews = {},
            onRetryReviews = {},
            onShowAllReviews = {},
            onHideAllReviews = {}
        )
    }
}
