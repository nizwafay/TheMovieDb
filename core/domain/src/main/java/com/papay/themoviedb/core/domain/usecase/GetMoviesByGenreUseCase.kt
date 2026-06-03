package com.papay.themoviedb.core.domain.usecase

import com.papay.themoviedb.core.domain.repository.MovieRepository
import com.papay.themoviedb.core.domain.result.DataResult
import com.papay.themoviedb.core.model.MoviePage

class GetMoviesByGenreUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(genreId: Int, page: Int): DataResult<MoviePage> {
        return movieRepository.getMoviesByGenre(
            genreId = genreId,
            page = page
        )
    }
}
