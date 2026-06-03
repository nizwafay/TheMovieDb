package com.papay.themoviedb.core.ui

import java.util.concurrent.atomic.AtomicLong

internal object UiMessageId {
    private val value = AtomicLong(0)

    fun next(): Long = value.incrementAndGet()
}
