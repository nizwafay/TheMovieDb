package com.papay.themoviedb.core.network.genre

import retrofit2.http.GET
import retrofit2.http.Query

interface GenreApiService {
    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("language") language: String = "en-US"
    ): GenreResponseDto
}
