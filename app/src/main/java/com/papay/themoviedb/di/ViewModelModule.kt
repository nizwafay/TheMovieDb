package com.papay.themoviedb.di

import com.papay.themoviedb.feature.genres.GenreListViewModel
import com.papay.themoviedb.feature.movies.MovieDetailViewModel
import com.papay.themoviedb.feature.movies.MovieListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::GenreListViewModel)
    viewModelOf(::MovieDetailViewModel)
    viewModelOf(::MovieListViewModel)
}
