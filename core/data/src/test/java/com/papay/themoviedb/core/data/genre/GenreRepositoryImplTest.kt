package com.papay.themoviedb.core.data.genre

import com.papay.themoviedb.core.data.coroutine.DispatcherProvider
import com.papay.themoviedb.core.data.genre.datasource.GenreLocalDataSource
import com.papay.themoviedb.core.data.genre.datasource.GenreRemoteDataSource
import com.papay.themoviedb.core.model.Genre
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class GenreRepositoryImplTest {
    private val genreLocalDataSource = mockk<GenreLocalDataSource>()
    private val genreRemoteDataSource = mockk<GenreRemoteDataSource>()
    private val testDispatcher = StandardTestDispatcher()
    private val repository = GenreRepositoryImpl(
        genreLocalDataSource = genreLocalDataSource,
        genreRemoteDataSource = genreRemoteDataSource,
        dispatcherProvider = TestDispatcherProvider(testDispatcher)
    )

    @Test
    fun `get genres saves remote genres and returns cached genres`() = runTest(testDispatcher) {
        coEvery { genreRemoteDataSource.getGenres() } returns RemoteGenres
        coEvery { genreLocalDataSource.upsertGenres(RemoteGenres) } returns Unit
        coEvery { genreLocalDataSource.getGenres() } returns CachedGenres

        val result = repository.getGenres()

        assertEquals(CachedGenres, result.data)
        assertNull(result.fallbackError)
        coVerifyOrder {
            genreRemoteDataSource.getGenres()
            genreLocalDataSource.upsertGenres(RemoteGenres)
            genreLocalDataSource.getGenres()
        }
    }

    @Test
    fun `get genres returns remote genres when cache is empty after saving`() = runTest(testDispatcher) {
        coEvery { genreRemoteDataSource.getGenres() } returns RemoteGenres
        coEvery { genreLocalDataSource.upsertGenres(RemoteGenres) } returns Unit
        coEvery { genreLocalDataSource.getGenres() } returns emptyList()

        val result = repository.getGenres()

        assertEquals(RemoteGenres, result.data)
        assertNull(result.fallbackError)
    }

    @Test
    fun `get genres returns cached genres and fallback error when remote fails`() = runTest(testDispatcher) {
        val remoteError = RuntimeException("Remote failed")
        coEvery { genreRemoteDataSource.getGenres() } throws remoteError
        coEvery { genreLocalDataSource.getGenres() } returns CachedGenres

        val result = repository.getGenres()

        assertEquals(CachedGenres, result.data)
        assertSame(remoteError, result.fallbackError)
        coVerify(exactly = 0) { genreLocalDataSource.upsertGenres(any()) }
    }

    @Test
    fun `get genres throws remote error when remote fails and cache is empty`() = runTest(testDispatcher) {
        val remoteError = RuntimeException("Remote failed")
        coEvery { genreRemoteDataSource.getGenres() } throws remoteError
        coEvery { genreLocalDataSource.getGenres() } returns emptyList()

        val error = assertThrows<RuntimeException> {
            repository.getGenres()
        }

        assertEquals(remoteError.message, error.message)
    }

    @Test
    fun `get genre returns local genre`() = runTest(testDispatcher) {
        coEvery { genreLocalDataSource.getGenre(id = 28) } returns RemoteGenres.first()

        val result = repository.getGenre(id = 28)

        assertEquals(RemoteGenres.first(), result)
        coVerify { genreLocalDataSource.getGenre(id = 28) }
        coVerify(exactly = 0) { genreRemoteDataSource.getGenres() }
    }

    private class TestDispatcherProvider(
        private val testDispatcher: CoroutineDispatcher
    ) : DispatcherProvider {
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val main: CoroutineDispatcher = testDispatcher
    }

    private suspend inline fun <reified T : Throwable> assertThrows(
        crossinline block: suspend () -> Unit
    ): T {
        return try {
            block()
            throw AssertionError("Expected ${T::class.java.simpleName} to be thrown.")
        } catch (throwable: Throwable) {
            if (throwable is T) {
                throwable
            } else {
                throw AssertionError(
                    "Expected ${T::class.java.simpleName}, but was ${throwable::class.java.simpleName}.",
                    throwable
                )
            }
        }
    }

    private companion object {
        val RemoteGenres = listOf(
            Genre(id = 28, name = "Action"),
            Genre(id = 35, name = "Comedy")
        )
        val CachedGenres = listOf(
            Genre(id = 18, name = "Drama")
        )
    }
}
