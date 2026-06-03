package com.papay.themoviedb.di

import com.papay.themoviedb.core.domain.usecase.GetGenresUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetGenresUseCase(genreRepository = get()) }
}
