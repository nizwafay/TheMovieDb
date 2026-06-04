package com.papay.themoviedb.core.data.di

import com.papay.themoviedb.core.data.coroutine.DefaultDispatcherProvider
import com.papay.themoviedb.core.data.coroutine.DispatcherProvider
import org.junit.Assert.assertSame
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class CoroutineModuleTest {

    @Test
    fun `provides default dispatcher provider`() {
        val koinApplication = startKoin {
            modules(coroutineModule)
        }

        try {
            val dispatcherProvider = koinApplication.koin.get<DispatcherProvider>()

            assertSame(DefaultDispatcherProvider, dispatcherProvider)
        } finally {
            stopKoin()
        }
    }
}
