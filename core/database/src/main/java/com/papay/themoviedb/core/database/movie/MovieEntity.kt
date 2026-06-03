package com.papay.themoviedb.core.database.movie

import androidx.room.Entity

@Entity(
    tableName = "movies",
    primaryKeys = ["genreId", "id"]
)
data class MovieEntity(
    val genreId: Int,
    val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?
)
