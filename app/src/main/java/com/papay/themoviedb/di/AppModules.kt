package com.papay.themoviedb.di

import com.papay.themoviedb.BuildConfig
import com.papay.themoviedb.core.data.di.dataModules
import com.papay.themoviedb.core.logging.di.loggingModule

val appModules = listOf(
    loggingModule(isDebug = BuildConfig.DEBUG)
) + dataModules(
    accessToken = BuildConfig.TMDB_ACCESS_TOKEN
) + listOf(
    useCaseModule,
    viewModelModule
)
