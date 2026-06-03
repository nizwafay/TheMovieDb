package com.papay.themoviedb.core.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.papay.themoviedb.core.network.genre.GenreApiService

object NetworkFactory {
    fun createMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    fun createOkHttpClient(accessToken: String): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(accessToken = accessToken))
            .build()
    }

    fun createRetrofit(
        moshi: Moshi,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConfig.BaseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    fun createGenreApiService(accessToken: String): GenreApiService {
        return createRetrofit(
            moshi = createMoshi(),
            okHttpClient = createOkHttpClient(accessToken = accessToken)
        ).create(GenreApiService::class.java)
    }
}
