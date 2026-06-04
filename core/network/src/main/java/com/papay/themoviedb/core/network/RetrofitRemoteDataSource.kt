package com.papay.themoviedb.core.network

import com.papay.themoviedb.core.domain.error.AppException
import com.papay.themoviedb.core.logging.AppLogger
import com.papay.themoviedb.core.logging.NoOpAppLogger
import java.io.IOException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

class RetrofitRemoteDataSource(
    private val logger: AppLogger = NoOpAppLogger
) {
    @Suppress("SwallowedException")
    suspend fun <T> execute(apiCall: suspend () -> T): T {
        return try {
            apiCall()
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            val appException = exception.toAppException()
            logger.logNetworkFailure(appException)
            throw appException
        }
    }
}

private fun AppLogger.logNetworkFailure(exception: AppException) {
    when (exception) {
        is AppException.MissingAccessToken,
        is AppException.InvalidAccessTokenType -> Unit
        is AppException.Unauthorized -> warning(
            tag = NetworkLogTag,
            message = "Request was rejected by the remote service."
        )
        is AppException.RateLimited -> warning(
            tag = NetworkLogTag,
            message = "Request was rate limited by the remote service."
        )
        is AppException.ServerError -> warning(
            tag = NetworkLogTag,
            message = "Remote service returned a server error."
        )
        is AppException.NetworkUnavailable -> debug(
            tag = NetworkLogTag,
            message = "Network is unavailable."
        )
        is AppException.Unexpected -> error(
            tag = NetworkLogTag,
            message = "Unexpected remote data source failure.",
            throwable = exception.cause
        )
    }
}

private fun Exception.toAppException(): AppException {
    return when (this) {
        is AppException -> this
        is HttpException -> toAppException()
        is IOException -> AppException.NetworkUnavailable(this)
        else -> AppException.Unexpected(this)
    }
}

private fun HttpException.toAppException(): AppException {
    return when (code()) {
        401, 403 -> AppException.Unauthorized(cause = this)
        429 -> AppException.RateLimited(cause = this)
        in 500..599 -> AppException.ServerError(cause = this)
        else -> AppException.Unexpected(this)
    }
}

private const val NetworkLogTag = "Network"
