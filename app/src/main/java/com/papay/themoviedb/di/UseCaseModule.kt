package com.papay.themoviedb.di

import com.papay.themoviedb.core.domain.usecase.GetGenreUseCase
import com.papay.themoviedb.core.domain.usecase.GetGenresUseCase
import com.papay.themoviedb.core.domain.usecase.GetMovieDetailUseCase
import com.papay.themoviedb.core.domain.usecase.GetMovieUseCase
import com.papay.themoviedb.core.domain.usecase.GetMovieYoutubeTrailerUseCase
import com.papay.themoviedb.core.domain.usecase.GetMovieReviewsUseCase
import com.papay.themoviedb.core.domain.usecase.GetMoviesByGenreUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetGenreUseCase(genreRepository = get()) }
    factory { GetGenresUseCase(genreRepository = get()) }
    factory { GetMovieDetailUseCase(movieRepository = get()) }
    factory { GetMovieUseCase(movieRepository = get()) }
    factory { GetMovieYoutubeTrailerUseCase(movieRepository = get()) }
    factory { GetMovieReviewsUseCase(movieRepository = get()) }
    factory { GetMoviesByGenreUseCase(movieRepository = get()) }
}
