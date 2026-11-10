package com.task.githubrepo.presentation.trending

import com.task.githubrepo.domain.model.TrendingRepo

sealed interface TrendingState {
    data object Loading : TrendingState
    data class Content(
        val repos: List<TrendingRepo>,
        val isRefreshing: Boolean = false,
        val expandedIds: Set<Long> = emptySet(),
    ) : TrendingState
    data class Error(val message: String) : TrendingState
}

sealed interface TrendingIntent {
    data object Load : TrendingIntent
    data object Refresh : TrendingIntent
    data object Retry : TrendingIntent
    data class ToggleExpand(val id: Long) : TrendingIntent
}

sealed interface TrendingEffect {
    data class ShowMessage(val text: String) : TrendingEffect
}
