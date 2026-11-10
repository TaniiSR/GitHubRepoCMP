package com.task.githubrepo.presentation.trending

import androidx.lifecycle.viewModelScope
import com.task.githubrepo.domain.model.DataResult
import com.task.githubrepo.domain.usecase.GetTrendingReposUseCase
import com.task.githubrepo.presentation.mvi.MviViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val GENERIC_ERROR = "Request failed please try again"

/**
 * The ViewModel does NOT load on construction: the screen must send [TrendingIntent.Load] once
 * when it appears. This keeps construction side-effect free and the initial state deterministic.
 */
class TrendingViewModel(
    private val getTrendingRepos: GetTrendingReposUseCase,
) : MviViewModel<TrendingState, TrendingIntent, TrendingEffect>(TrendingState.Loading) {

    private var loadJob: Job? = null

    override fun onIntent(intent: TrendingIntent) {
        when (intent) {
            TrendingIntent.Load -> {
                if (state.value is TrendingState.Loading && loadJob?.isActive != true) {
                    fetch(forceRefresh = false)
                }
            }
            TrendingIntent.Retry -> {
                if (state.value is TrendingState.Error) {
                    setState { TrendingState.Loading }
                    fetch(forceRefresh = true)
                }
            }
            TrendingIntent.Refresh -> {
                val current = state.value
                if (current is TrendingState.Content && !current.isRefreshing) {
                    setState { (this as TrendingState.Content).copy(isRefreshing = true) }
                    fetch(forceRefresh = true)
                }
            }
            is TrendingIntent.ToggleExpand -> setState {
                if (this is TrendingState.Content) {
                    copy(
                        expandedIds = if (intent.id in expandedIds) expandedIds - intent.id
                        else expandedIds + intent.id,
                    )
                } else this
            }
        }
    }

    private fun fetch(forceRefresh: Boolean) {
        loadJob = viewModelScope.launch {
            when (val result = getTrendingRepos(forceRefresh)) {
                is DataResult.Success ->
                    if (result.data.isEmpty()) {
                        setState { TrendingState.Error(GENERIC_ERROR) }
                    } else {
                        setState {
                            TrendingState.Content(
                                repos = result.data,
                                expandedIds = (this as? TrendingState.Content)?.expandedIds.orEmpty(),
                            )
                        }
                    }
                is DataResult.Failure -> {
                    val message = result.error.toMessage()
                    if (state.value is TrendingState.Content) {
                        setState { (this as TrendingState.Content).copy(isRefreshing = false) }
                        sendEffect(TrendingEffect.ShowMessage(message))
                    } else {
                        setState { TrendingState.Error(message) }
                    }
                }
            }
        }
    }
}
