package com.papay.themoviedb.core.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow

@Composable
fun LazyListLoadMoreEffect(
    listState: LazyListState,
    itemCount: Int,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    enabled: Boolean = true,
    threshold: Int = DefaultLoadMoreThreshold
) {
    val shouldLoadMore = remember(listState, threshold) {
        derivedStateOf {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            totalItems > 0 && lastVisibleIndex >= totalItems - threshold
        }
    }

    LaunchedEffect(itemCount, canLoadMore, isLoadingMore, enabled) {
        snapshotFlow { shouldLoadMore.value }
            .collect { shouldLoad ->
                if (enabled && shouldLoad && canLoadMore && !isLoadingMore) {
                    onLoadMore()
                }
            }
    }
}

@Composable
fun LazyGridLoadMoreEffect(
    gridState: LazyGridState,
    itemCount: Int,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    enabled: Boolean = true,
    threshold: Int = DefaultLoadMoreThreshold
) {
    val shouldLoadMore = remember(gridState, threshold) {
        derivedStateOf {
            val lastVisibleIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = gridState.layoutInfo.totalItemsCount
            totalItems > 0 && lastVisibleIndex >= totalItems - threshold
        }
    }

    LaunchedEffect(itemCount, canLoadMore, isLoadingMore, enabled) {
        snapshotFlow { shouldLoadMore.value }
            .collect { shouldLoad ->
                if (enabled && shouldLoad && canLoadMore && !isLoadingMore) {
                    onLoadMore()
                }
            }
    }
}

private const val DefaultLoadMoreThreshold = 6
