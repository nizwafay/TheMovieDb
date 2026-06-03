package com.papay.themoviedb.core.model

data class MoviePage(
    val movies: List<Movie>,
    val page: Int,
    val totalPages: Int
)
