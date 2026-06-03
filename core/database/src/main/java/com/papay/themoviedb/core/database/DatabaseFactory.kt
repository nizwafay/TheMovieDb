package com.papay.themoviedb.core.database

import android.content.Context
import androidx.room.Room

object DatabaseFactory {
    fun create(context: Context): TheMovieDbDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = TheMovieDbDatabase::class.java,
            name = TheMovieDbDatabase.DatabaseName
        ).build()
    }
}
