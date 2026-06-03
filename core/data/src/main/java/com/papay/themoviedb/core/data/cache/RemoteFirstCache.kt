package com.papay.themoviedb.core.data.cache

import com.papay.themoviedb.core.domain.result.DataResult

suspend fun <Model> remoteFirstCache(
    fetchRemote: suspend () -> List<Model>,
    saveRemote: suspend (List<Model>) -> Unit,
    readCache: suspend () -> List<Model>
): DataResult<List<Model>> {
    val remoteResult = runCatching { fetchRemote() }
        .onSuccess { remoteItems -> saveRemote(remoteItems) }

    val cachedItems = readCache()
    return if (cachedItems.isNotEmpty()) {
        DataResult(
            data = cachedItems,
            fallbackError = remoteResult.exceptionOrNull()
        )
    } else {
        DataResult(data = remoteResult.getOrThrow())
    }
}
