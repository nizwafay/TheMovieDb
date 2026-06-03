package com.papay.themoviedb.core.domain.usecase

import com.papay.themoviedb.core.domain.repository.MovieRepository
import com.papay.themoviedb.core.model.MovieDetail

class GetMovieDetailUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): MovieDetail {
        return movieRepository.getMovieDetail(movieId = movieId)
    }
}
