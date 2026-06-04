package com.papay.themoviedb.core.logging.di

import com.papay.themoviedb.core.logging.AndroidAppLogger
import com.papay.themoviedb.core.logging.AppLogger
import com.papay.themoviedb.core.logging.NoOpAppLogger
import org.koin.dsl.module

fun loggingModule(isDebug: Boolean) = module {
    single<AppLogger> {
        if (isDebug) {
            AndroidAppLogger()
        } else {
            NoOpAppLogger
        }
    }
}
