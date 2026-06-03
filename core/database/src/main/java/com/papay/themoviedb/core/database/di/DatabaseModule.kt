package com.papay.themoviedb.core.database.di

import com.papay.themoviedb.core.database.DatabaseFactory
import com.papay.themoviedb.core.database.TheMovieDbDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { DatabaseFactory.create(context = androidContext()) }
    single { get<TheMovieDbDatabase>().genreDao() }
    single { get<TheMovieDbDatabase>().movieDao() }
}
