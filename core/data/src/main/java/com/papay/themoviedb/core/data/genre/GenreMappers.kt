package com.papay.themoviedb.core.data.genre

import com.papay.themoviedb.core.database.genre.GenreEntity
import com.papay.themoviedb.core.model.Genre
import com.papay.themoviedb.core.network.genre.GenreDto

fun GenreEntity.toModel(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

fun GenreDto.toModel(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

fun GenreDto.toEntity(): GenreEntity {
    return toModel().toEntity()
}

fun Genre.toEntity(): GenreEntity {
    return GenreEntity(
        id = id,
        name = name
    )
}
