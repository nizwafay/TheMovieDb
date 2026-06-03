package com.papay.themoviedb.di

import com.papay.themoviedb.BuildConfig
import com.papay.themoviedb.core.data.di.dataModules

val appModules = dataModules(
    accessToken = BuildConfig.TMDB_ACCESS_TOKEN
) + listOf(
    useCaseModule,
    viewModelModule
)
