package com.papay.themoviedb.core.network.genre

data class GenreResponseDto(
    val genres: List<GenreDto>
)

data class GenreDto(
    val id: Int,
    val name: String
)
