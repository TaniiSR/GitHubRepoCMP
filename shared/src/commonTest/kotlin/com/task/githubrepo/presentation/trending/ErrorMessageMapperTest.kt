package com.task.githubrepo.presentation.trending

import com.task.githubrepo.domain.model.DataError
import kotlin.test.Test
import kotlin.test.assertEquals

class ErrorMessageMapperTest {
    private val generic = "Request failed please try again"
    private val offline = "Please check your internet connection"

    @Test
    fun mapsEveryErrorToOriginalCopy() {
        val table = mapOf(
            DataError.Forbidden to "You don't have access to this information",
            DataError.NotFound to "Not Found",
            DataError.BadGateway to "Bad Gateway",
            DataError.NoConnection to offline,
            DataError.Timeout to offline,
            DataError.Server(500) to generic,
            DataError.Unknown to generic,
        )
        table.forEach { (error, expected) -> assertEquals(expected, error.toMessage(), "for $error") }
    }
}
