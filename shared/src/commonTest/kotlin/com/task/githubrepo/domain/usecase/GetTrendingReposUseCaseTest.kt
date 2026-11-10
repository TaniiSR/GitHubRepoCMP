package com.task.githubrepo.domain.usecase

import com.task.githubrepo.domain.model.DataError
import com.task.githubrepo.domain.model.DataResult
import com.task.githubrepo.domain.model.TrendingRepo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class GetTrendingReposUseCaseTest {
    private val repo = TrendingRepo(1, "n", "o/n", null, null, 5, "o", null)

    @Test
    fun defaultsToNoForceRefreshAndReturnsRepositoryResult() = runTest {
        val fake = FakeTrendingRepository().apply { enqueue(DataResult.Success(listOf(repo))) }
        val result = GetTrendingReposUseCase(fake)()
        assertEquals(listOf(false), fake.calls)
        assertEquals(DataResult.Success(listOf(repo)), result)
    }

    @Test
    fun forwardsForceRefreshTrue() = runTest {
        val fake = FakeTrendingRepository().apply { enqueue(DataResult.Success(listOf(repo))) }
        GetTrendingReposUseCase(fake)(forceRefresh = true)
        assertEquals(listOf(true), fake.calls)
    }

    @Test
    fun passesFailureThrough() = runTest {
        val fake = FakeTrendingRepository().apply { enqueue(DataResult.Failure(DataError.Forbidden)) }
        assertEquals(DataResult.Failure(DataError.Forbidden), GetTrendingReposUseCase(fake)())
    }
}
