package com.task.githubrepo.data

import com.task.githubrepo.data.local.LocalDataSource
import com.task.githubrepo.data.remote.RemoteDataSource
import com.task.githubrepo.domain.model.DataError
import com.task.githubrepo.domain.model.DataResult
import com.task.githubrepo.domain.model.TrendingRepo
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

private fun repo(id: Long) = TrendingRepo(id, "n$id", "o/n$id", null, null, id.toInt(), "o", null)

private class FakeRemote(var result: DataResult<List<TrendingRepo>>) : RemoteDataSource {
    var calls = 0
    override suspend fun fetchTrending(): DataResult<List<TrendingRepo>> {
        calls++
        return result
    }
}

private class FakeLocal(var stored: List<TrendingRepo>? = null) : LocalDataSource {
    var saves = 0
    override fun get(): List<TrendingRepo>? = stored?.ifEmpty { null }
    override fun save(repos: List<TrendingRepo>) {
        saves++
        stored = repos
    }
}

class TrendingRepositoryImplTest {
    private val cached = listOf(repo(1), repo(2))
    private val fresh = listOf(repo(7), repo(8), repo(9))

    private fun success(list: List<TrendingRepo>) = DataResult.Success(list)

    @Test
    fun cacheHitWithoutRefreshSkipsRemote() = runTest {
        val remote = FakeRemote(success(fresh))
        val repository = TrendingRepositoryImpl(remote, FakeLocal(cached))
        assertEquals(success(cached), repository.get(forceRefresh = false))
        assertEquals(0, remote.calls)
    }

    @Test
    fun emptyCacheWithoutRefreshGoesToRemoteAndSaves() = runTest {
        val remote = FakeRemote(success(fresh))
        val local = FakeLocal(null)
        val repository = TrendingRepositoryImpl(remote, local)
        assertEquals(success(fresh), repository.get(forceRefresh = false))
        assertEquals(1, remote.calls)
        assertEquals(fresh, local.stored)
    }

    @Test
    fun forceRefreshWithCacheHitsRemoteAndOverwritesCache() = runTest {
        val remote = FakeRemote(success(fresh))
        val local = FakeLocal(cached)
        val repository = TrendingRepositoryImpl(remote, local)
        assertEquals(success(fresh), repository.get(forceRefresh = true))
        assertEquals(1, remote.calls)
        assertEquals(fresh, local.stored)
    }

    @Test
    fun forceRefreshWithEmptyCacheHitsRemote() = runTest {
        val remote = FakeRemote(success(fresh))
        val repository = TrendingRepositoryImpl(remote, FakeLocal(null))
        repository.get(forceRefresh = true)
        assertEquals(1, remote.calls)
    }

    @Test
    fun failureIsReturnedAsIsAndCacheIsKept() = runTest {
        val failure = DataResult.Failure(DataError.Server(500))
        val remote = FakeRemote(failure)
        val local = FakeLocal(cached)
        val repository = TrendingRepositoryImpl(remote, local)
        assertEquals(failure, repository.get(forceRefresh = true))
        assertEquals(cached, local.stored)
        assertEquals(0, local.saves)
    }

    @Test
    fun failureWithEmptyCacheIsReturnedAndNothingSaved() = runTest {
        val failure = DataResult.Failure(DataError.NoConnection)
        val local = FakeLocal(null)
        val repository = TrendingRepositoryImpl(FakeRemote(failure), local)
        assertEquals(failure, repository.get(forceRefresh = false))
        assertEquals(0, local.saves)
    }

    @Test
    fun emptyRemoteListIsSuccessAndSavedThenCountsAsCacheMiss() = runTest {
        val remote = FakeRemote(success(emptyList()))
        val local = FakeLocal(cached)
        val repository = TrendingRepositoryImpl(remote, local)
        assertEquals(success(emptyList()), repository.get(forceRefresh = true))
        assertEquals(1, local.saves)
        assertEquals(emptyList(), local.stored)

        remote.result = success(fresh)
        assertEquals(success(fresh), repository.get(forceRefresh = false))
        assertEquals(2, remote.calls)
    }

    @Test
    fun secondNonForcedCallIsServedFromCacheFilledByFirst() = runTest {
        val remote = FakeRemote(success(fresh))
        val repository = TrendingRepositoryImpl(remote, FakeLocal(null))
        repository.get(forceRefresh = false)
        assertEquals(success(fresh), repository.get(forceRefresh = false))
        assertEquals(1, remote.calls)
    }
}
