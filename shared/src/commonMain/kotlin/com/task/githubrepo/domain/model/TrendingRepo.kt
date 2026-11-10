package com.task.githubrepo.domain.model

data class TrendingRepo(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val language: String?,
    val stars: Int,
    val ownerLogin: String,
    val ownerAvatarUrl: String?,
)
