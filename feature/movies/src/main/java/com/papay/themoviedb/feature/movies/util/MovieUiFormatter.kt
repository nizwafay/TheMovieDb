package com.papay.themoviedb.feature.movies.util

import com.papay.themoviedb.core.model.Movie

fun Movie.posterUrl(): String? {
    return posterPath?.let { path -> "$TmdbPosterBaseUrl$path" }
}

fun Movie.releaseYear(): String? {
    return releaseDate?.takeIf { date -> date.length >= ReleaseYearLength }?.take(ReleaseYearLength)
}

private const val TmdbPosterBaseUrl = "https://image.tmdb.org/t/p/w342"
private const val ReleaseYearLength = 4
