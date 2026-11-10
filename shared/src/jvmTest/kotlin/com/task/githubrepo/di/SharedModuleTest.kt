package com.task.githubrepo.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.task.githubrepo.data.local.db.TrendingDatabase
import com.task.githubrepo.data.remote.SEARCH_JSON
import com.task.githubrepo.presentation.trending.TrendingIntent
import com.task.githubrepo.presentation.trending.TrendingState
import com.task.githubrepo.presentation.trending.TrendingViewModel
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.koin.core.context.stopKoin
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class SharedModuleTest {

    private val testPlatformModule = module {
        single<HttpClientEngine> {
            MockEngine {
                respond(
                    content = SEARCH_JSON,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
        }
        single<SqlDriver> {
            JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also { TrendingDatabase.Schema.create(it) }
        }
    }

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun viewModelResolvesAndLoadReachesContentThroughTheRealWiring() = runBlocking {
        val koin = koinApplication { modules(sharedModule, testPlatformModule) }.koin

        val vm = koin.get<TrendingViewModel>()
        assertEquals(TrendingState.Loading, vm.state.value)

        vm.onIntent(TrendingIntent.Load)
        val state = withTimeout(10_000) { vm.state.first { it !is TrendingState.Loading } }

        val content = assertIs<TrendingState.Content>(state)
        assertEquals("golang/go", content.repos.first().fullName)
    }
}
