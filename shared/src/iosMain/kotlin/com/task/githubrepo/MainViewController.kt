package com.task.githubrepo

import androidx.compose.ui.window.ComposeUIViewController
import com.task.githubrepo.di.initKoin
import com.task.githubrepo.ui.App
import org.koin.mp.KoinPlatform

fun MainViewController() = ComposeUIViewController(
    configure = {
        // Guard against double init (e.g. multiple view controllers / SwiftUI re-creation).
        if (runCatching { KoinPlatform.getKoinOrNull() }.getOrNull() == null) initKoin()
    },
) { App() }
