package com.papay.themoviedb.navigation

object AppDestinations {
    const val Genres = "genres"
    const val MovieGenreIdArg = "genreId"
    const val MoviesByGenre = "movies/{$MovieGenreIdArg}"

    fun moviesByGenre(genreId: Int): String {
        return "movies/$genreId"
    }
}
