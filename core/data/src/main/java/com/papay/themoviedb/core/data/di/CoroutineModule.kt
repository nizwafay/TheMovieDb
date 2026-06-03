package com.papay.themoviedb.core.data.di

import com.papay.themoviedb.core.data.coroutine.DefaultDispatcherProvider
import com.papay.themoviedb.core.data.coroutine.DispatcherProvider
import org.koin.dsl.module

internal val coroutineModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider }
}
