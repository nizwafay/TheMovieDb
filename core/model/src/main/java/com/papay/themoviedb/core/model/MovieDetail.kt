package com.papay.themoviedb.core.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val releaseDate: String?,
    val genres: List<Genre>,
    val spokenLanguages: List<SpokenLanguage>,
    val voteAverage: Double,
    val voteCount: Int
)

data class SpokenLanguage(
    val name: String
)
