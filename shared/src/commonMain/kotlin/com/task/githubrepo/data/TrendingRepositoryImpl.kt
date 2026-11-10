package com.task.githubrepo.data

import com.task.githubrepo.data.local.LocalDataSource
import com.task.githubrepo.data.remote.RemoteDataSource
import com.task.githubrepo.domain.model.DataResult
import com.task.githubrepo.domain.model.TrendingRepo
import com.task.githubrepo.domain.repository.TrendingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrendingRepositoryImpl(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource,
) : TrendingRepository {
    override suspend fun get(forceRefresh: Boolean): DataResult<List<TrendingRepo>> =
        fetchIfNeeded(forceRefresh)

    private suspend fun fetchIfNeeded(forceRefresh: Boolean): DataResult<List<TrendingRepo>> {
        if (!forceRefresh) {
            withContext(Dispatchers.Default) { local.get() }?.let { return DataResult.Success(it) }
        }
        val result = remote.fetchTrending()
        if (result is DataResult.Success) withContext(Dispatchers.Default) { local.save(result.data) }
        return result
    }
}
