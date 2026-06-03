package com.papay.themoviedb.core.data.di

import com.papay.themoviedb.core.data.genre.GenreRepositoryImpl
import com.papay.themoviedb.core.data.movie.MovieRepositoryImpl
import com.papay.themoviedb.core.domain.repository.GenreRepository
import com.papay.themoviedb.core.domain.repository.MovieRepository
import org.koin.dsl.module

internal val repositoryModule = module {
    single<GenreRepository> {
        GenreRepositoryImpl(
            genreLocalDataSource = get(),
            genreRemoteDataSource = get(),
            dispatcherProvider = get()
        )
    }
    single<MovieRepository> {
        MovieRepositoryImpl(
            movieLocalDataSource = get(),
            movieRemoteDataSource = get(),
            dispatcherProvider = get()
        )
    }
}
