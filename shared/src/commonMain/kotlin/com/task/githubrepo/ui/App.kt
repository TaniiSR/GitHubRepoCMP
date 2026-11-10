package com.task.githubrepo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.task.githubrepo.presentation.trending.TrendingEffect
import com.task.githubrepo.presentation.trending.TrendingIntent
import com.task.githubrepo.presentation.trending.TrendingViewModel
import org.koin.compose.viewmodel.koinViewModel

/** Root composable. Koin must already be started by the platform entry point. */
@Composable
fun App() {
    MaterialTheme(colorScheme = lightColorScheme(background = Color.White, surface = Color.White)) {
        val viewModel = koinViewModel<TrendingViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(viewModel) { viewModel.onIntent(TrendingIntent.Load) }
        LaunchedEffect(viewModel) {
            viewModel.effects.collect { effect ->
                when (effect) {
                    is TrendingEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.text)
                }
            }
        }

        Scaffold(
            containerColor = Color.White,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = Modifier.fillMaxSize().background(Color.White),
        ) { padding ->
            TrendingScreen(state, Modifier.padding(padding), viewModel::onIntent)
        }
    }
}
