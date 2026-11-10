package com.task.githubrepo.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.task.githubrepo.presentation.trending.TrendingIntent
import com.task.githubrepo.presentation.trending.TrendingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingScreen(
    state: TrendingState,
    modifier: Modifier = Modifier,
    onIntent: (TrendingIntent) -> Unit,
) {
    when (state) {
        TrendingState.Loading -> LoadingView(modifier)
        is TrendingState.Content -> PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onIntent(TrendingIntent.Refresh) },
            modifier = modifier.fillMaxSize(),
        ) {
            LazyColumn(Modifier.fillMaxSize()) {
                items(state.repos, key = { it.id }) { repo ->
                    RepoRow(
                        repo = repo,
                        expanded = repo.id in state.expandedIds,
                        onClick = { onIntent(TrendingIntent.ToggleExpand(repo.id)) },
                    )
                }
            }
        }
        is TrendingState.Error -> ErrorView(state.message, onRetry = { onIntent(TrendingIntent.Retry) }, modifier = modifier)
    }
}
