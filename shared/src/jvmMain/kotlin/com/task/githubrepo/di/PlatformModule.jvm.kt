package com.task.githubrepo.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.task.githubrepo.data.local.db.TrendingDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<HttpClientEngine> { OkHttp.create() }
    single<SqlDriver> {
        JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also { TrendingDatabase.Schema.create(it) }
    }
}
