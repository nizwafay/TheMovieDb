package com.papay.themoviedb

import android.app.Application
import com.papay.themoviedb.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TheMovieDbApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@TheMovieDbApplication)
            modules(appModules)
        }
    }
}
