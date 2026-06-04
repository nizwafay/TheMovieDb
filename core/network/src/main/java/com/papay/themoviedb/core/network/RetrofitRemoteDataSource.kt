package com.papay.themoviedb.core.network

import com.papay.themoviedb.core.domain.error.AppException
import java.io.IOException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

class RetrofitRemoteDataSource {
    suspend fun <T> execute(apiCall: suspend () -> T): T {
        return try {
            apiCall()
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw exception.toAppException()
        }
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
        401, 403 -> AppException.Unauthorized()
        429 -> AppException.RateLimited()
        in 500..599 -> AppException.ServerError()
        else -> AppException.Unexpected(this)
    }
}
