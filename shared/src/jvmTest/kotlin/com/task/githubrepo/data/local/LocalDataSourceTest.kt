package com.task.githubrepo.data.local

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.task.githubrepo.data.local.db.TrendingDatabase
import com.task.githubrepo.domain.model.TrendingRepo
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LocalDataSourceTest {

    private fun newSource(): LocalDataSource {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        TrendingDatabase.Schema.create(driver)
        return SqlDelightLocalDataSource(TrendingDatabase(driver))
    }

    private fun repo(id: Long, description: String? = "d$id", language: String? = "Kotlin") = TrendingRepo(
        id = id,
        name = "n$id",
        fullName = "o/n$id",
        description = description,
        language = language,
        stars = (id * 10).toInt(),
        ownerLogin = "o",
        ownerAvatarUrl = if (id % 2 == 0L) null else "https://a/$id",
    )

    @Test
    fun emptyCacheReturnsNull() = runTest {
        assertNull(newSource().get())
    }

    @Test
    fun saveThenGetReturnsSameOrderAndContent() = runTest {
        val source = newSource()
        val repos = listOf(repo(30), repo(10, description = null, language = null), repo(20))
        source.save(repos)
        assertEquals(repos, source.get())
    }

    @Test
    fun secondSaveOverwritesWithNoLeftovers() = runTest {
        val source = newSource()
        source.save(listOf(repo(1), repo(2), repo(3)))
        val second = listOf(repo(9), repo(8))
        source.save(second)
        assertEquals(second, source.get())
    }

    @Test
    fun savingEmptyListMakesCacheEmpty() = runTest {
        val source = newSource()
        source.save(listOf(repo(1)))
        source.save(emptyList())
        assertNull(source.get())
    }
}
