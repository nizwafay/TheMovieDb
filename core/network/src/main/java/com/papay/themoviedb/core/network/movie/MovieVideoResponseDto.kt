package com.papay.themoviedb.core.network.movie

data class MovieVideoResponseDto(
    val results: List<MovieVideoDto>
)

data class MovieVideoDto(
    val key: String,
    val name: String,
    val site: String,
    val type: String
)
