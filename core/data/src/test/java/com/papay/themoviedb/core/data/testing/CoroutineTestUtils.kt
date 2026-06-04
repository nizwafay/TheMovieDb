package com.papay.themoviedb.core.data.testing

import com.papay.themoviedb.core.data.coroutine.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher

class TestDispatcherProvider(
    private val testDispatcher: CoroutineDispatcher
) : DispatcherProvider {
    override val io: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
    override val main: CoroutineDispatcher = testDispatcher
}

suspend inline fun <reified T : Throwable> assertThrows(
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
