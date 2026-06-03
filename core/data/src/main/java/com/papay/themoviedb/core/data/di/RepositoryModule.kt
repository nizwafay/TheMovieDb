package com.papay.themoviedb.core.data.di

import com.papay.themoviedb.core.data.genre.GenreRepositoryImpl
import com.papay.themoviedb.core.domain.repository.GenreRepository
import org.koin.dsl.module

internal val repositoryModule = module {
    single<GenreRepository> {
        GenreRepositoryImpl(
            genreLocalDataSource = get(),
            genreRemoteDataSource = get(),
            dispatcherProvider = get()
        )
    }
}
