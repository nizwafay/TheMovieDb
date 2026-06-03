package com.papay.themoviedb.core.network.movie

import com.squareup.moshi.Json

data class MovieResponseDto(
    val page: Int,
    @param:Json(name = "total_pages") val totalPages: Int,
    val results: List<MovieDto>
)

data class MovieDto(
    val id: Int,
    val title: String,
    @param:Json(name = "poster_path") val posterPath: String?,
    @param:Json(name = "release_date") val releaseDate: String?
)
