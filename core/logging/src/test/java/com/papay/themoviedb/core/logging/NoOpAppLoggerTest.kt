package com.papay.themoviedb.core.logging

import org.junit.Test

class NoOpAppLoggerTest {
    @Test
    fun `logging methods do nothing`() {
        NoOpAppLogger.debug(tag = "Test", message = "Debug")
        NoOpAppLogger.warning(tag = "Test", message = "Warning")
        NoOpAppLogger.error(
            tag = "Test",
            message = "Error",
            throwable = IllegalStateException()
        )
    }
}
