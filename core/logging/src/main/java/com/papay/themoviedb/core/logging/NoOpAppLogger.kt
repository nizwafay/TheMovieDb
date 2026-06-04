package com.papay.themoviedb.core.logging

object NoOpAppLogger : AppLogger {
    override fun debug(tag: String, message: String) = Unit

    override fun warning(tag: String, message: String) = Unit

    override fun error(tag: String, message: String, throwable: Throwable?) = Unit
}
