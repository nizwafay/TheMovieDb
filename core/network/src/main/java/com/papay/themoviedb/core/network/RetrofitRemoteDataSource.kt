package com.papay.themoviedb.core.network

import com.papay.themoviedb.core.domain.error.AppException
import java.io.IOException
import retrofit2.HttpException

class RetrofitRemoteDataSource {
    suspend fun <T> execute(apiCall: suspend () -> T): T {
        return try {
            apiCall()
        } catch (exception: HttpException) {
            when (exception.code()) {
                401, 403 -> throw AppException.Unauthorized()
                429 -> throw AppException.RateLimited()
                in 500..599 -> throw AppException.ServerError()
                else -> throw AppException.Unexpected(exception)
            }
        } catch (exception: IOException) {
            throw AppException.NetworkUnavailable(exception)
        } catch (exception: AppException) {
            throw exception
        } catch (exception: Throwable) {
            throw AppException.Unexpected(exception)
        }
    }
}
