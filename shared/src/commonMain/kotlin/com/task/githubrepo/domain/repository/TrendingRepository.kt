package com.task.githubrepo.domain.repository

import com.task.githubrepo.domain.model.DataResult
import com.task.githubrepo.domain.model.TrendingRepo

interface TrendingRepository {
    suspend fun get(forceRefresh: Boolean): DataResult<List<TrendingRepo>>
}
