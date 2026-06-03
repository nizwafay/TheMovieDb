package com.papay.themoviedb.core.domain.usecase

import com.papay.themoviedb.core.domain.repository.MovieRepository
import com.papay.themoviedb.core.model.MovieReviewPage

class GetMovieReviewsUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int, page: Int): MovieReviewPage {
        return movieRepository.getMovieReviews(
            movieId = movieId,
            page = page
        )
    }
}
