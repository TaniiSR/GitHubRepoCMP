package com.task.githubrepo.domain.usecase

import com.task.githubrepo.domain.model.DataResult
import com.task.githubrepo.domain.model.TrendingRepo
import com.task.githubrepo.domain.repository.TrendingRepository

class FakeTrendingRepository : TrendingRepository {
    private val queue = ArrayDeque<DataResult<List<TrendingRepo>>>()
    val calls = mutableListOf<Boolean>()

    fun enqueue(vararg results: DataResult<List<TrendingRepo>>) {
        queue.addAll(results)
    }

    override suspend fun get(forceRefresh: Boolean): DataResult<List<TrendingRepo>> {
        calls += forceRefresh
        return queue.removeFirst()
    }
}
