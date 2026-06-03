package com.papay.themoviedb.core.network.di

import com.papay.themoviedb.core.network.NetworkFactory
import com.papay.themoviedb.core.network.RetrofitRemoteDataSource
import org.koin.dsl.module

fun networkModule(accessToken: String) = module {
    single { RetrofitRemoteDataSource() }
    single { NetworkFactory.createGenreApiService(accessToken = accessToken) }
}
