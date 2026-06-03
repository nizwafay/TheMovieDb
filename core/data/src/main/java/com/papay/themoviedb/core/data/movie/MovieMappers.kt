package com.papay.themoviedb.core.data.movie

import com.papay.themoviedb.core.database.movie.MovieEntity
import com.papay.themoviedb.core.data.genre.toModel
import com.papay.themoviedb.core.model.Movie
import com.papay.themoviedb.core.model.MovieDetail
import com.papay.themoviedb.core.model.MoviePage
import com.papay.themoviedb.core.model.MovieReview
import com.papay.themoviedb.core.model.MovieReviewPage
import com.papay.themoviedb.core.model.MovieVideo
import com.papay.themoviedb.core.model.SpokenLanguage
import com.papay.themoviedb.core.network.movie.MovieDetailDto
import com.papay.themoviedb.core.network.movie.MovieDto
import com.papay.themoviedb.core.network.movie.MovieResponseDto
import com.papay.themoviedb.core.network.movie.MovieReviewDto
import com.papay.themoviedb.core.network.movie.MovieReviewResponseDto
import com.papay.themoviedb.core.network.movie.SpokenLanguageDto
import com.papay.themoviedb.core.network.movie.MovieVideoDto

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

fun MovieDetailDto.toModel(): MovieDetail {
    return MovieDetail(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        genres = genres.map { genre -> genre.toModel() },
        spokenLanguages = spokenLanguages.map { language -> language.toModel() },
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0
    )
}

fun SpokenLanguageDto.toModel(): SpokenLanguage {
    return SpokenLanguage(name = englishName ?: name.orEmpty())
}

fun MovieReviewResponseDto.toModel(): MovieReviewPage {
    return MovieReviewPage(
        reviews = results.map { review -> review.toModel() },
        page = page,
        totalPages = totalPages,
        totalResults = totalResults
    )
}

fun MovieReviewDto.toModel(): MovieReview {
    return MovieReview(
        id = id,
        author = author,
        content = content,
        createdAt = createdAt,
        rating = authorDetails?.rating
    )
}

fun MovieVideoDto.toModel(): MovieVideo {
    return MovieVideo(
        key = key,
        name = name,
        site = site,
        type = type
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
