package com.task.githubrepo.data.local

import com.task.githubrepo.data.local.db.TrendingDatabase
import com.task.githubrepo.domain.model.TrendingRepo

interface LocalDataSource {
    /** Cached repos in saved order, or null when the cache is empty. */
    fun get(): List<TrendingRepo>?

    /** Replaces the whole cache atomically; position is the list index. */
    fun save(repos: List<TrendingRepo>)
}

class SqlDelightLocalDataSource(database: TrendingDatabase) : LocalDataSource {
    private val queries = database.trendingRepoEntityQueries

    override fun get(): List<TrendingRepo>? =
        queries.selectAll().executeAsList()
            .map {
                TrendingRepo(
                    id = it.id,
                    name = it.name,
                    fullName = it.fullName,
                    description = it.description,
                    language = it.language,
                    stars = it.stars.toInt(),
                    ownerLogin = it.ownerLogin,
                    ownerAvatarUrl = it.ownerAvatarUrl,
                )
            }
            .ifEmpty { null }

    override fun save(repos: List<TrendingRepo>) {
        queries.transaction {
            queries.deleteAll()
            repos.forEachIndexed { index, r ->
                queries.insertRepo(
                    position = index.toLong(),
                    id = r.id,
                    name = r.name,
                    fullName = r.fullName,
                    description = r.description,
                    language = r.language,
                    stars = r.stars.toLong(),
                    ownerLogin = r.ownerLogin,
                    ownerAvatarUrl = r.ownerAvatarUrl,
                )
            }
        }
    }
}
