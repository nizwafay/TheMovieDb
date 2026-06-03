package com.papay.themoviedb.core.network.movie

import com.papay.themoviedb.core.network.genre.GenreDto
import com.squareup.moshi.Json

data class MovieDetailDto(
    val id: Int,
    val title: String,
    val overview: String,
    @param:Json(name = "release_date") val releaseDate: String?,
    val genres: List<GenreDto>,
    @param:Json(name = "spoken_languages") val spokenLanguages: List<SpokenLanguageDto>,
    @param:Json(name = "vote_average") val voteAverage: Double?,
    @param:Json(name = "vote_count") val voteCount: Int?
)

data class SpokenLanguageDto(
    @param:Json(name = "english_name") val englishName: String?,
    val name: String?
)
