package com.papay.themoviedb.core.data.cache

import com.papay.themoviedb.core.data.testing.assertThrows
import com.papay.themoviedb.core.model.Genre
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class RemoteFirstCacheTest {
    private val cacheOperations = mockk<CacheOperations>()

    @Test
    fun `remote success saves remote data and returns cached data`() = runTest {
        coEvery { cacheOperations.fetchRemote() } returns RemoteGenres
        coEvery { cacheOperations.saveRemote(RemoteGenres) } returns Unit
        coEvery { cacheOperations.readCache() } returns CachedGenres

        val result = remoteFirstCache(
            fetchRemote = { cacheOperations.fetchRemote() },
            saveRemote = { genres -> cacheOperations.saveRemote(genres) },
            readCache = { cacheOperations.readCache() }
        )

        assertEquals(CachedGenres, result.data)
        assertNull(result.fallbackError)
        coVerifyOrder {
            cacheOperations.fetchRemote()
            cacheOperations.saveRemote(RemoteGenres)
            cacheOperations.readCache()
        }
    }

    @Test
    fun `remote success returns remote data when cache is empty after saving`() = runTest {
        coEvery { cacheOperations.fetchRemote() } returns RemoteGenres
        coEvery { cacheOperations.saveRemote(RemoteGenres) } returns Unit
        coEvery { cacheOperations.readCache() } returns emptyList()

        val result = remoteFirstCache(
            fetchRemote = { cacheOperations.fetchRemote() },
            saveRemote = { genres -> cacheOperations.saveRemote(genres) },
            readCache = { cacheOperations.readCache() }
        )

        assertEquals(RemoteGenres, result.data)
        assertNull(result.fallbackError)
        coVerifyOrder {
            cacheOperations.fetchRemote()
            cacheOperations.saveRemote(RemoteGenres)
            cacheOperations.readCache()
        }
    }

    @Test
    fun `remote failure returns cached data with fallback error when cache exists`() = runTest {
        val remoteError = RuntimeException("Remote failed")
        coEvery { cacheOperations.fetchRemote() } throws remoteError
        coEvery { cacheOperations.readCache() } returns CachedGenres

        val result = remoteFirstCache(
            fetchRemote = { cacheOperations.fetchRemote() },
            saveRemote = { genres -> cacheOperations.saveRemote(genres) },
            readCache = { cacheOperations.readCache() }
        )

        assertEquals(CachedGenres, result.data)
        assertSame(remoteError, result.fallbackError)
        coVerify(exactly = 0) { cacheOperations.saveRemote(any()) }
        coVerifyOrder {
            cacheOperations.fetchRemote()
            cacheOperations.readCache()
        }
    }

    @Test
    fun `remote failure throws remote error when cache is empty`() = runTest {
        val remoteError = RuntimeException("Remote failed")
        coEvery { cacheOperations.fetchRemote() } throws remoteError
        coEvery { cacheOperations.readCache() } returns emptyList()

        val error = assertThrows<RuntimeException> {
            remoteFirstCache(
                fetchRemote = { cacheOperations.fetchRemote() },
                saveRemote = { genres -> cacheOperations.saveRemote(genres) },
                readCache = { cacheOperations.readCache() }
            )
        }

        assertSame(remoteError, error)
        coVerify(exactly = 0) { cacheOperations.saveRemote(any()) }
        coVerifyOrder {
            cacheOperations.fetchRemote()
            cacheOperations.readCache()
        }
    }

    private interface CacheOperations {
        suspend fun fetchRemote(): List<Genre>
        suspend fun saveRemote(genres: List<Genre>)
        suspend fun readCache(): List<Genre>
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
