package com.papay.themoviedb.di

import com.papay.themoviedb.core.domain.usecase.GetGenreUseCase
import com.papay.themoviedb.core.domain.usecase.GetGenresUseCase
import com.papay.themoviedb.core.domain.usecase.GetMoviesByGenreUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetGenreUseCase(genreRepository = get()) }
    factory { GetGenresUseCase(genreRepository = get()) }
    factory { GetMoviesByGenreUseCase(movieRepository = get()) }
}
