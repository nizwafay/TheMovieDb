package com.papay.themoviedb.core.model

data class MovieReview(
    val id: String,
    val author: String,
    val content: String,
    val createdAt: String?,
    val rating: Double?
)

data class MovieReviewPage(
    val reviews: List<MovieReview>,
    val page: Int,
    val totalPages: Int,
    val totalResults: Int
)
