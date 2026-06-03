package com.papay.themoviedb.core.domain.usecase

import com.papay.themoviedb.core.domain.repository.GenreRepository
import com.papay.themoviedb.core.domain.result.DataResult
import com.papay.themoviedb.core.model.Genre

class GetGenresUseCase(
    private val genreRepository: GenreRepository
) {
    suspend operator fun invoke(): DataResult<List<Genre>> = genreRepository.getGenres()
}
