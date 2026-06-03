package com.papay.themoviedb.navigation

object AppDestinations {
    const val Genres = "genres"
    const val MovieGenreIdArg = "genreId"
    const val MovieIdArg = "movieId"
    const val MoviesByGenre = "movies/{$MovieGenreIdArg}"
    const val MovieDetail = "movies/detail/{$MovieIdArg}"

    fun moviesByGenre(genreId: Int): String {
        return "movies/$genreId"
    }

    fun movieDetail(movieId: Int): String {
        return "movies/detail/$movieId"
    }
}
