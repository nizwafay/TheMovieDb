package com.papay.themoviedb.core.domain.result

data class DataResult<T>(
    val data: T,
    val fallbackError: Throwable? = null
)
