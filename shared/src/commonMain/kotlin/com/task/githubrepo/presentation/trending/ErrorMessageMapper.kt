package com.task.githubrepo.presentation.trending

import com.task.githubrepo.domain.model.DataError

private const val GENERIC = "Request failed please try again"
private const val OFFLINE = "Please check your internet connection"

fun DataError.toMessage(): String = when (this) {
    DataError.Forbidden -> "You don't have access to this information"
    DataError.NotFound -> "Not Found"
    DataError.BadGateway -> "Bad Gateway"
    DataError.NoConnection, DataError.Timeout -> OFFLINE
    is DataError.Server, DataError.Unknown -> GENERIC
}
