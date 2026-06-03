package com.papay.themoviedb.core.data.genre.remote

import com.papay.themoviedb.core.data.genre.datasource.GenreRemoteDataSource
import com.papay.themoviedb.core.data.genre.toModel
import com.papay.themoviedb.core.model.Genre
import com.papay.themoviedb.core.network.RetrofitRemoteDataSource
import com.papay.themoviedb.core.network.genre.GenreApiService

class GenreRemoteDataSourceImpl(
    private val genreApiService: GenreApiService,
    private val retrofitRemoteDataSource: RetrofitRemoteDataSource
) : GenreRemoteDataSource {
    override suspend fun getGenres(): List<Genre> {
        return retrofitRemoteDataSource.execute {
            genreApiService.getGenres()
        }.genres.map { genre -> genre.toModel() }
    }
}
