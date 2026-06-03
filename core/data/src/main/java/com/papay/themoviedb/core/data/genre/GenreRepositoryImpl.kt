package com.papay.themoviedb.core.data.genre

import com.papay.themoviedb.core.data.cache.remoteFirstCache
import com.papay.themoviedb.core.data.coroutine.DispatcherProvider
import com.papay.themoviedb.core.data.genre.datasource.GenreLocalDataSource
import com.papay.themoviedb.core.data.genre.datasource.GenreRemoteDataSource
import com.papay.themoviedb.core.domain.repository.GenreRepository
import com.papay.themoviedb.core.domain.result.DataResult
import com.papay.themoviedb.core.model.Genre
import kotlinx.coroutines.withContext

class GenreRepositoryImpl(
    private val genreLocalDataSource: GenreLocalDataSource,
    private val genreRemoteDataSource: GenreRemoteDataSource,
    private val dispatcherProvider: DispatcherProvider
) : GenreRepository {
    override suspend fun getGenres(): DataResult<List<Genre>> = withContext(dispatcherProvider.io) {
        remoteFirstCache(
            fetchRemote = { genreRemoteDataSource.getGenres() },
            saveRemote = { genres -> genreLocalDataSource.upsertGenres(genres) },
            readCache = { genreLocalDataSource.getGenres() }
        )
    }
}
