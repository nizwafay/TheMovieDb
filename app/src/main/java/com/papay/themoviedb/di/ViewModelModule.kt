package com.papay.themoviedb.di

import com.papay.themoviedb.feature.genres.GenreListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::GenreListViewModel)
}
