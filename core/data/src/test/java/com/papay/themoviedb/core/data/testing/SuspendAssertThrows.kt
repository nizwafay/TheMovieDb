package com.papay.themoviedb.core.data.testing

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
