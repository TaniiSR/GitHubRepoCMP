package com.task.githubrepo.data.remote.dto

import com.task.githubrepo.domain.model.TrendingRepo

fun SearchResponseDto.toDomain(): List<TrendingRepo> =
    items.orEmpty().mapNotNull { it.toDomainOrNull() }

private fun RepoItemDto.toDomainOrNull(): TrendingRepo? {
    val id = id ?: return null
    val name = name ?: return null
    val fullName = fullName ?: return null
    val ownerLogin = owner?.login ?: return null
    return TrendingRepo(
        id = id,
        name = name,
        fullName = fullName,
        description = description,
        language = language,
        stars = stargazersCount ?: 0,
        ownerLogin = ownerLogin,
        ownerAvatarUrl = owner.avatarUrl,
    )
}
