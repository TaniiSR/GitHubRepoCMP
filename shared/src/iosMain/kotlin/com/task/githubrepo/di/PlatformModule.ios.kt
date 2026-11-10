package com.task.githubrepo.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.task.githubrepo.data.local.db.TrendingDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<HttpClientEngine> { Darwin.create() }
    single<SqlDriver> { NativeSqliteDriver(TrendingDatabase.Schema, "trending.db") }
}
