package com.papay.themoviedb.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.papay.themoviedb.core.database.genre.GenreDao
import com.papay.themoviedb.core.database.genre.GenreEntity

@Database(
    entities = [GenreEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TheMovieDbDatabase : RoomDatabase() {
    abstract fun genreDao(): GenreDao

    companion object {
        const val DatabaseName = "the_movie_db"
    }
}
