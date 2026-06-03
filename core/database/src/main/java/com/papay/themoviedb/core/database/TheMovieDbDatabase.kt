package com.papay.themoviedb.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.papay.themoviedb.core.database.genre.GenreDao
import com.papay.themoviedb.core.database.genre.GenreEntity
import com.papay.themoviedb.core.database.movie.MovieDao
import com.papay.themoviedb.core.database.movie.MovieEntity

@Database(
    entities = [
        GenreEntity::class,
        MovieEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TheMovieDbDatabase : RoomDatabase() {
    abstract fun genreDao(): GenreDao
    abstract fun movieDao(): MovieDao

    companion object {
        const val DatabaseName = "the_movie_db"
    }
}
