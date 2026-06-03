package com.papay.themoviedb.core.domain.usecase

import com.papay.themoviedb.core.domain.repository.MovieRepository
import com.papay.themoviedb.core.model.Movie

class GetMovieUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(id: Int): Movie? {
        return movieRepository.getMovie(id = id)
    }
}
