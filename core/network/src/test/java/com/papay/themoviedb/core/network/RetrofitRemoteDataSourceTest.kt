package com.papay.themoviedb.core.network

import com.papay.themoviedb.core.domain.error.AppException
import com.papay.themoviedb.core.logging.AppLogger
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class RetrofitRemoteDataSourceTest {
    private val logger = mockk<AppLogger>()
    private val remoteDataSource = RetrofitRemoteDataSource(logger = logger)
    private val apiCall = mockk<ApiCall>()

    init {
        every { logger.debug(any(), any()) } just Runs
        every { logger.warning(any(), any()) } just Runs
        every { logger.error(any(), any(), any()) } just Runs
    }

    @Test
    fun `execute returns api result when api call succeeds`() = runTest {
        coEvery { apiCall.execute() } returns ApiResult

        val result = remoteDataSource.execute { apiCall.execute() }

        assertEquals(ApiResult, result)
        coVerify(exactly = 1) { apiCall.execute() }
        verify(exactly = 0) { logger.debug(any(), any()) }
        verify(exactly = 0) { logger.warning(any(), any()) }
        verify(exactly = 0) { logger.error(any(), any(), any()) }
    }

    @Test
    fun `execute maps unauthorized http status to unauthorized exception`() = runTest {
        listOf(401, 403).forEach { statusCode ->
            coEvery { apiCall.execute() } throws httpException(statusCode)

            assertThrows<AppException.Unauthorized> {
                remoteDataSource.execute { apiCall.execute() }
            }
        }

        verify(exactly = 2) {
            logger.warning(
                tag = "Network",
                message = "Request was rejected by the remote service."
            )
        }
    }

    @Test
    fun `execute maps rate limit status to rate limited exception`() = runTest {
        coEvery { apiCall.execute() } throws httpException(429)

        assertThrows<AppException.RateLimited> {
            remoteDataSource.execute { apiCall.execute() }
        }

        verify(exactly = 1) {
            logger.warning(
                tag = "Network",
                message = "Request was rate limited by the remote service."
            )
        }
    }

    @Test
    fun `execute maps server error status to server error exception`() = runTest {
        listOf(500, 502, 599).forEach { statusCode ->
            coEvery { apiCall.execute() } throws httpException(statusCode)

            assertThrows<AppException.ServerError> {
                remoteDataSource.execute { apiCall.execute() }
            }
        }

        verify(exactly = 3) {
            logger.warning(
                tag = "Network",
                message = "Remote service returned a server error."
            )
        }
    }

    @Test
    fun `execute maps unhandled http status to unexpected exception`() = runTest {
        val httpException = httpException(400)
        coEvery { apiCall.execute() } throws httpException

        val exception = assertThrows<AppException.Unexpected> {
            remoteDataSource.execute { apiCall.execute() }
        }

        assertSame(httpException, exception.cause)
        verify(exactly = 1) {
            logger.error(
                tag = "Network",
                message = "Unexpected remote data source failure.",
                throwable = httpException
            )
        }
    }

    @Test
    fun `execute maps io exception to network unavailable exception`() = runTest {
        val ioException = IOException("No connection")
        coEvery { apiCall.execute() } throws ioException

        val exception = assertThrows<AppException.NetworkUnavailable> {
            remoteDataSource.execute { apiCall.execute() }
        }

        assertSame(ioException, exception.cause)
        verify(exactly = 1) {
            logger.debug(
                tag = "Network",
                message = "Network is unavailable."
            )
        }
    }

    @Test
    fun `execute rethrows app exception unchanged`() = runTest {
        val appException = AppException.MissingAccessToken()
        coEvery { apiCall.execute() } throws appException

        val exception = assertThrows<AppException.MissingAccessToken> {
            remoteDataSource.execute { apiCall.execute() }
        }

        assertSame(appException, exception)
        verify(exactly = 0) { logger.debug(any(), any()) }
        verify(exactly = 0) { logger.warning(any(), any()) }
        verify(exactly = 0) { logger.error(any(), any(), any()) }
    }

    @Test
    fun `execute maps unexpected throwable to unexpected exception`() = runTest {
        val throwable = IllegalStateException("Broken response")
        coEvery { apiCall.execute() } throws throwable

        val exception = assertThrows<AppException.Unexpected> {
            remoteDataSource.execute { apiCall.execute() }
        }

        assertSame(throwable, exception.cause)
        verify(exactly = 1) {
            logger.error(
                tag = "Network",
                message = "Unexpected remote data source failure.",
                throwable = throwable
            )
        }
    }

    private fun httpException(statusCode: Int): HttpException {
        return HttpException(
            Response.error<Unit>(
                statusCode,
                "Error".toResponseBody()
            )
        )
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

    private interface ApiCall {
        suspend fun execute(): String
    }

    private companion object {
        const val ApiResult = "result"
    }
}
