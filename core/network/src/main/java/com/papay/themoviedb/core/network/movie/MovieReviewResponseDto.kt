package com.papay.themoviedb.core.network.movie

import com.squareup.moshi.Json

data class MovieReviewResponseDto(
    val page: Int,
    @param:Json(name = "total_pages") val totalPages: Int,
    @param:Json(name = "total_results") val totalResults: Int,
    val results: List<MovieReviewDto>
)

data class MovieReviewDto(
    val id: String,
    val author: String,
    val content: String,
    @param:Json(name = "created_at") val createdAt: String?,
    @param:Json(name = "author_details") val authorDetails: AuthorDetailsDto?
)

data class AuthorDetailsDto(
    val rating: Double?
)
