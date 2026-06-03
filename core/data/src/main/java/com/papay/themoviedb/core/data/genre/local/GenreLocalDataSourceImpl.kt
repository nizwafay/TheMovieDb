package com.papay.themoviedb.core.data.genre.local

import com.papay.themoviedb.core.database.genre.GenreDao
import com.papay.themoviedb.core.data.genre.datasource.GenreLocalDataSource
import com.papay.themoviedb.core.data.genre.toEntity
import com.papay.themoviedb.core.data.genre.toModel
import com.papay.themoviedb.core.model.Genre

class GenreLocalDataSourceImpl(
    private val genreDao: GenreDao
) : GenreLocalDataSource {
    override suspend fun getGenres(): List<Genre> {
        return genreDao.getGenres().map { genre -> genre.toModel() }
    }

    override suspend fun getGenre(id: Int): Genre? {
        return genreDao.getGenre(id = id)?.toModel()
    }

    override suspend fun upsertGenres(genres: List<Genre>) {
        genreDao.upsertGenres(genres.map { genre -> genre.toEntity() })
    }
}
