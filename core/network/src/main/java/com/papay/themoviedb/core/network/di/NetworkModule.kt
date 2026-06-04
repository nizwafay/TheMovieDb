package com.papay.themoviedb.core.network.di

import com.papay.themoviedb.core.network.NetworkFactory
import com.papay.themoviedb.core.network.RetrofitRemoteDataSource
import com.papay.themoviedb.core.network.genre.GenreApiService
import com.papay.themoviedb.core.network.movie.MovieApiService
import org.koin.dsl.module
import retrofit2.Retrofit

fun networkModule(accessToken: String) = module {
    single { NetworkFactory.createMoshi() }
    single { NetworkFactory.createOkHttpClient(accessToken = accessToken) }
    single { NetworkFactory.createRetrofit(moshi = get(), okHttpClient = get()) }
    single { RetrofitRemoteDataSource(logger = get()) }
    single { get<Retrofit>().create(GenreApiService::class.java) }
    single { get<Retrofit>().create(MovieApiService::class.java) }
}
