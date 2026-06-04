package com.papay.themoviedb.core.network

import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import mockwebserver3.RecordedRequest
import org.junit.rules.ExternalResource
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class MockWebServerRule : ExternalResource() {
    private var _server: MockWebServer? = null

    val server: MockWebServer
        get() = checkNotNull(_server)

    override fun before() {
        _server = MockWebServer().apply {
            start()
        }
    }

    override fun after() {
        _server?.close()
        _server = null
    }

    inline fun <reified T : Any> createService(): T {
        return Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(NetworkFactory.createMoshi()))
            .build()
            .create(T::class.java)
    }
}

fun MockWebServer.enqueueJson(body: String) {
    enqueue(
        MockResponse.Builder()
            .code(200)
            .setHeader("Content-Type", "application/json")
            .body(body)
            .build()
    )
}

fun MockWebServer.takeRecordedRequest(): RecordedRequest {
    return checkNotNull(takeRequest(timeout = 1, unit = TimeUnit.SECONDS))
}
