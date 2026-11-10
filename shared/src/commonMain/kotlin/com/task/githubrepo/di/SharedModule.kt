package com.task.githubrepo.di

import app.cash.sqldelight.db.SqlDriver
import com.task.githubrepo.data.TrendingRepositoryImpl
import com.task.githubrepo.data.local.LocalDataSource
import com.task.githubrepo.data.local.SqlDelightLocalDataSource
import com.task.githubrepo.data.local.db.TrendingDatabase
import com.task.githubrepo.data.remote.KtorRemoteDataSource
import com.task.githubrepo.data.remote.RemoteDataSource
import com.task.githubrepo.data.remote.createHttpClient
import com.task.githubrepo.domain.repository.TrendingRepository
import com.task.githubrepo.domain.usecase.GetTrendingReposUseCase
import com.task.githubrepo.presentation.trending.TrendingViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Platform-independent wiring; needs [HttpClientEngine] and [SqlDriver] from a platform module. */
val sharedModule: Module = module {
    single<HttpClient> { createHttpClient(get<HttpClientEngine>()) }
    single<RemoteDataSource> { KtorRemoteDataSource(get()) }
    single<LocalDataSource> { SqlDelightLocalDataSource(TrendingDatabase(get<SqlDriver>())) }
    single<TrendingRepository> { TrendingRepositoryImpl(get(), get()) }
    single { GetTrendingReposUseCase(get()) }
    viewModel { TrendingViewModel(get()) }
}
