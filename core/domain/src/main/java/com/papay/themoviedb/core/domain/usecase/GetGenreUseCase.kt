package com.papay.themoviedb.core.domain.usecase

import com.papay.themoviedb.core.domain.repository.GenreRepository
import com.papay.themoviedb.core.model.Genre

class GetGenreUseCase(
    private val genreRepository: GenreRepository
) {
    suspend operator fun invoke(id: Int): Genre? {
        return genreRepository.getGenre(id = id)
    }
}
