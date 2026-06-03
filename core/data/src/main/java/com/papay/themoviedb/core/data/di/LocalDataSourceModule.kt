package com.papay.themoviedb.core.data.di

import com.papay.themoviedb.core.data.genre.datasource.GenreLocalDataSource
import com.papay.themoviedb.core.data.genre.local.GenreLocalDataSourceImpl
import com.papay.themoviedb.core.data.movie.datasource.MovieLocalDataSource
import com.papay.themoviedb.core.data.movie.local.MovieLocalDataSourceImpl
import org.koin.dsl.module

internal val localDataSourceModule = module {
    single<GenreLocalDataSource> {
        GenreLocalDataSourceImpl(genreDao = get())
    }
    single<MovieLocalDataSource> {
        MovieLocalDataSourceImpl(movieDao = get())
    }
}
