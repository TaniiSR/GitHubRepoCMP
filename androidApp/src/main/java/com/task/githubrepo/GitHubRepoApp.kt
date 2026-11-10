package com.task.githubrepo

import android.app.Application
import com.task.githubrepo.di.initKoin
import org.koin.android.ext.koin.androidContext

class GitHubRepoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin { androidContext(this@GitHubRepoApp) }
    }
}
