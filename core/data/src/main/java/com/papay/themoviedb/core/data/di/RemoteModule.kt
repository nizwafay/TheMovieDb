package com.papay.themoviedb.core.data.di

import com.papay.themoviedb.core.data.genre.datasource.GenreRemoteDataSource
import com.papay.themoviedb.core.data.genre.remote.GenreRemoteDataSourceImpl
import org.koin.dsl.module

internal val remoteModule = module {
    single<GenreRemoteDataSource> {
        GenreRemoteDataSourceImpl(
            genreApiService = get(),
            retrofitRemoteDataSource = get()
        )
    }
}
