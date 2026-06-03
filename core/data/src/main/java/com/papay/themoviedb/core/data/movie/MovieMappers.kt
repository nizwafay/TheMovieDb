package com.papay.themoviedb.core.data.movie

import com.papay.themoviedb.core.database.movie.MovieEntity
import com.papay.themoviedb.core.model.Movie
import com.papay.themoviedb.core.model.MoviePage
import com.papay.themoviedb.core.network.movie.MovieDto
import com.papay.themoviedb.core.network.movie.MovieResponseDto

fun MovieEntity.toModel(): Movie {
    return Movie(
        id = id,
        title = title,
        posterPath = posterPath,
        releaseDate = releaseDate
    )
}

fun MovieDto.toModel(): Movie {
    return Movie(
        id = id,
        title = title,
        posterPath = posterPath,
        releaseDate = releaseDate
    )
}

fun MovieResponseDto.toModel(): MoviePage {
    return MoviePage(
        movies = results.map { movie -> movie.toModel() },
        page = page,
        totalPages = totalPages
    )
}

fun Movie.toEntity(genreId: Int): MovieEntity {
    return MovieEntity(
        genreId = genreId,
        id = id,
        title = title,
        posterPath = posterPath,
        releaseDate = releaseDate
    )
}
