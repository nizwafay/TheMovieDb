package com.papay.themoviedb.core.data.di

import com.papay.themoviedb.core.data.coroutine.DispatcherProvider
import com.papay.themoviedb.core.data.genre.GenreRepositoryImpl
import com.papay.themoviedb.core.data.genre.datasource.GenreLocalDataSource
import com.papay.themoviedb.core.data.genre.datasource.GenreRemoteDataSource
import com.papay.themoviedb.core.data.genre.local.GenreLocalDataSourceImpl
import com.papay.themoviedb.core.data.genre.remote.GenreRemoteDataSourceImpl
import com.papay.themoviedb.core.data.movie.MovieRepositoryImpl
import com.papay.themoviedb.core.data.movie.datasource.MovieLocalDataSource
import com.papay.themoviedb.core.data.movie.datasource.MovieRemoteDataSource
import com.papay.themoviedb.core.data.movie.local.MovieLocalDataSourceImpl
import com.papay.themoviedb.core.data.movie.remote.MovieRemoteDataSourceImpl
import com.papay.themoviedb.core.database.genre.GenreDao
import com.papay.themoviedb.core.database.movie.MovieDao
import com.papay.themoviedb.core.domain.repository.GenreRepository
import com.papay.themoviedb.core.domain.repository.MovieRepository
import com.papay.themoviedb.core.network.RetrofitRemoteDataSource
import com.papay.themoviedb.core.network.genre.GenreApiService
import com.papay.themoviedb.core.network.movie.MovieApiService
import io.mockk.mockk
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class DataModulesTest {

    @Test
    fun `local data source module binds local data source implementations`() {
        val koinApplication = startKoin {
            modules(
                module {
                    single { mockk<GenreDao>() }
                    single { mockk<MovieDao>() }
                },
                localDataSourceModule
            )
        }

        try {
            assertTrue(koinApplication.koin.get<GenreLocalDataSource>() is GenreLocalDataSourceImpl)
            assertTrue(koinApplication.koin.get<MovieLocalDataSource>() is MovieLocalDataSourceImpl)
        } finally {
            stopKoin()
        }
    }

    @Test
    fun `remote module binds remote data source implementations`() {
        val koinApplication = startKoin {
            modules(
                module {
                    single { mockk<GenreApiService>() }
                    single { mockk<MovieApiService>() }
                    single { mockk<RetrofitRemoteDataSource>() }
                },
                remoteModule
            )
        }

        try {
            assertTrue(koinApplication.koin.get<GenreRemoteDataSource>() is GenreRemoteDataSourceImpl)
            assertTrue(koinApplication.koin.get<MovieRemoteDataSource>() is MovieRemoteDataSourceImpl)
        } finally {
            stopKoin()
        }
    }

    @Test
    fun `repository module binds repository implementations`() {
        val koinApplication = startKoin {
            modules(
                module {
                    single { mockk<GenreLocalDataSource>() }
                    single { mockk<GenreRemoteDataSource>() }
                    single { mockk<MovieLocalDataSource>() }
                    single { mockk<MovieRemoteDataSource>() }
                    single { mockk<DispatcherProvider>() }
                },
                repositoryModule
            )
        }

        try {
            assertTrue(koinApplication.koin.get<GenreRepository>() is GenreRepositoryImpl)
            assertTrue(koinApplication.koin.get<MovieRepository>() is MovieRepositoryImpl)
        } finally {
            stopKoin()
        }
    }
}
