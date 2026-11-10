package com.task.githubrepo.domain.usecase

import com.task.githubrepo.domain.model.DataResult
import com.task.githubrepo.domain.model.TrendingRepo
import com.task.githubrepo.domain.repository.TrendingRepository

class GetTrendingReposUseCase(private val repository: TrendingRepository) {
    suspend operator fun invoke(forceRefresh: Boolean = false): DataResult<List<TrendingRepo>> =
        repository.get(forceRefresh)
}
