package com.task.githubrepo.data.remote

import com.task.githubrepo.data.remote.dto.SearchResponseDto
import com.task.githubrepo.data.remote.dto.toDomain
import com.task.githubrepo.domain.model.DataError
import com.task.githubrepo.domain.model.DataResult
import com.task.githubrepo.domain.model.TrendingRepo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.ContentConvertException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException

interface RemoteDataSource {
    suspend fun fetchTrending(): DataResult<List<TrendingRepo>>
}

class KtorRemoteDataSource(private val client: HttpClient) : RemoteDataSource {

    override suspend fun fetchTrending(): DataResult<List<TrendingRepo>> =
        try {
            // Same request as the original Retrofit @GET("search/repositories?q=language=+sort:stars").
            val response = client.get("search/repositories?q=language=+sort:stars")
            response.toResult()
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpRequestTimeoutException) {
            DataResult.Failure(DataError.Timeout)
        } catch (e: ConnectTimeoutException) {
            DataResult.Failure(DataError.Timeout)
        } catch (e: SocketTimeoutException) {
            DataResult.Failure(DataError.Timeout)
        } catch (e: Exception) {
            DataResult.Failure(DataError.NoConnection)
        }

    private suspend fun HttpResponse.toResult(): DataResult<List<TrendingRepo>> {
        val code = status.value
        return when {
            code in 200..299 -> try {
                DataResult.Success(body<SearchResponseDto>().toDomain())
            } catch (e: CancellationException) {
                throw e
            } catch (e: SerializationException) {
                DataResult.Failure(DataError.Unknown)
            } catch (e: ContentConvertException) {
                DataResult.Failure(DataError.Unknown)
            } catch (e: IllegalArgumentException) {
                DataResult.Failure(DataError.Unknown)
            }
            code == 403 -> DataResult.Failure(DataError.Forbidden)
            code == 404 -> DataResult.Failure(DataError.NotFound)
            code == 502 -> DataResult.Failure(DataError.BadGateway)
            code == 504 -> DataResult.Failure(DataError.NoConnection)
            code in 400..500 -> DataResult.Failure(DataError.Server(code))
            else -> DataResult.Failure(DataError.Unknown)
        }
    }
}
