package com.task.githubrepo.data.remote

import com.task.githubrepo.domain.model.DataError
import com.task.githubrepo.domain.model.DataResult
import com.task.githubrepo.domain.model.TrendingRepo
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class RemoteDataSourceTest {

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")

    private fun sourceFor(
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> io.ktor.client.request.HttpResponseData,
    ): RemoteDataSource = KtorRemoteDataSource(createHttpClient(MockEngine(handler)))

    private suspend fun failureFor(status: Int): DataError {
        val result = sourceFor { respondError(HttpStatusCode.fromValue(status)) }.fetchTrending()
        return assertIs<DataResult.Failure>(result).error
    }

    private suspend fun failureForThrowing(t: Throwable): DataError {
        val result = sourceFor { throw t }.fetchTrending()
        return assertIs<DataResult.Failure>(result).error
    }

    @Test
    fun ok200ParsesAndMapsThreeItems() = runTest {
        val result = sourceFor { respond(SEARCH_JSON, HttpStatusCode.OK, jsonHeaders) }.fetchTrending()
        val data = assertIs<DataResult.Success<List<TrendingRepo>>>(result).data
        assertEquals(3, data.size)
        assertEquals("golang/go", data.first().fullName)
    }

    @Test
    fun status403IsForbidden() = runTest { assertEquals(DataError.Forbidden, failureFor(403)) }

    @Test
    fun status404IsNotFound() = runTest { assertEquals(DataError.NotFound, failureFor(404)) }

    @Test
    fun status502IsBadGateway() = runTest { assertEquals(DataError.BadGateway, failureFor(502)) }

    @Test
    fun status504IsNoConnection() = runTest { assertEquals(DataError.NoConnection, failureFor(504)) }

    @Test
    fun otherClientAndServerCodesAreServerWithCode() = runTest {
        for (code in listOf(400, 401, 422, 500)) {
            assertEquals(DataError.Server(code), failureFor(code), "status $code")
        }
    }

    @Test
    fun status501And503AreUnknown() = runTest {
        assertEquals(DataError.Unknown, failureFor(501))
        assertEquals(DataError.Unknown, failureFor(503))
    }

    @Test
    fun requestTimeoutIsTimeout() = runTest {
        val e = failureForThrowing(
            HttpRequestTimeoutException("https://api.github.com/", 15_000),
        )
        assertEquals(DataError.Timeout, e)
    }

    @Test
    fun connectTimeoutIsTimeout() = runTest {
        val e = failureForThrowing(
            ConnectTimeoutException("https://api.github.com/", 10_000),
        )
        assertEquals(DataError.Timeout, e)
    }

    @Test
    fun ioExceptionIsNoConnection() = runTest {
        assertEquals(DataError.NoConnection, failureForThrowing(IOException("offline")))
    }

    @Test
    fun genericExceptionIsNoConnection() = runTest {
        assertEquals(DataError.NoConnection, failureForThrowing(RuntimeException("boom")))
    }

    @Test
    fun malformedJsonOn200IsUnknown() = runTest {
        val result = sourceFor { respond("{not json", HttpStatusCode.OK, jsonHeaders) }.fetchTrending()
        assertEquals(DataError.Unknown, assertIs<DataResult.Failure>(result).error)
    }

    @Test
    fun sendsGetToSearchRepositoriesWithOriginalQuery() = runTest {
        var captured: HttpRequestData? = null
        sourceFor {
            captured = it
            respond(SEARCH_JSON, HttpStatusCode.OK, jsonHeaders)
        }.fetchTrending()
        val request = assertIs<HttpRequestData>(captured)
        assertEquals(HttpMethod.Get, request.method)
        assertEquals("api.github.com", request.url.host)
        assertEquals("/search/repositories", request.url.encodedPath)
        // Original Retrofit call: search/repositories?q=language=+sort:stars ('+' is a space)
        assertEquals("language= sort:stars", request.url.parameters["q"])
        assertEquals("q=language=+sort:stars", request.url.encodedQuery)
    }

    @Test
    fun cancellationPropagatesAndIsNotTurnedIntoDataError() = runTest {
        val started = CompletableDeferred<Unit>()
        val source = sourceFor {
            started.complete(Unit)
            awaitCancellation()
        }
        val job = async(start = CoroutineStart.UNDISPATCHED) { source.fetchTrending() }
        started.await()
        job.cancel()
        assertFailsWith<CancellationException> { job.await() }
    }

    @Test
    fun cancellationExceptionThrownByCallIsRethrown() = runTest {
        val source = sourceFor { throw CancellationException("cancelled") }
        assertFailsWith<CancellationException> { source.fetchTrending() }
    }
}
