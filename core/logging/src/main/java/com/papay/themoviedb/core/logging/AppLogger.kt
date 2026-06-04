package com.papay.themoviedb.core.logging

interface AppLogger {
    fun debug(tag: String, message: String)
    fun warning(tag: String, message: String)
    fun error(tag: String, message: String, throwable: Throwable? = null)
}
