package com.task.githubrepo.domain.model

sealed interface DataError {
    data object NoConnection : DataError
    data object Timeout : DataError
    data object Forbidden : DataError
    data object NotFound : DataError
    data object BadGateway : DataError
    data class Server(val code: Int) : DataError
    data object Unknown : DataError
}
