package com.task.githubrepo.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    @SerialName("total_count") val totalCount: Int? = null,
    @SerialName("incomplete_results") val incompleteResults: Boolean? = null,
    val items: List<RepoItemDto>? = null,
)

@Serializable
data class RepoItemDto(
    val id: Long? = null,
    val name: String? = null,
    @SerialName("full_name") val fullName: String? = null,
    val description: String? = null,
    val language: String? = null,
    @SerialName("stargazers_count") val stargazersCount: Int? = null,
    val owner: OwnerDto? = null,
)

@Serializable
data class OwnerDto(
    val login: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
)
