package com.task.githubrepo.data.remote

import com.task.githubrepo.data.remote.dto.SearchResponseDto
import com.task.githubrepo.data.remote.dto.toDomain
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SearchResponseDtoTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun mapsFirstItemExactly() {
        val repos = json.decodeFromString<SearchResponseDto>(SEARCH_JSON).toDomain()
        assertEquals(3, repos.size)
        val first = repos.first()
        assertEquals(23096959L, first.id)
        assertEquals("go", first.name)
        assertEquals("golang/go", first.fullName)
        assertEquals("The Go programming language", first.description)
        assertEquals("Go", first.language)
        assertEquals(108415, first.stars)
        assertEquals("golang", first.ownerLogin)
        assertEquals("https://avatars.githubusercontent.com/u/4314092?v=4", first.ownerAvatarUrl)
    }

    @Test
    fun dropsInvalidItemsAndIgnoresUnknownFields() {
        val repos = json.decodeFromString<SearchResponseDto>(SEARCH_JSON_WITH_INVALID_ITEMS).toDomain()
        assertEquals(listOf(23096959L, 34526884L, 44838949L, 3L), repos.map { it.id })
    }

    @Test
    fun keepsNullDescriptionAndLanguageNull() {
        val repo = json.decodeFromString<SearchResponseDto>(SEARCH_JSON_WITH_INVALID_ITEMS)
            .toDomain().first { it.id == 3L }
        assertNull(repo.description)
        assertNull(repo.language)
        assertNull(repo.ownerAvatarUrl)
    }

    @Test
    fun nullStarsBecomeZero() {
        val json2 = """{"items":[{"id":9,"name":"n","full_name":"o/n","owner":{"login":"o"}}]}"""
        val repos = json.decodeFromString<SearchResponseDto>(json2).toDomain()
        assertEquals(0, repos.single().stars)
    }

    @Test
    fun missingItemsYieldsEmptyList() {
        assertEquals(emptyList(), json.decodeFromString<SearchResponseDto>("{}").toDomain())
    }
}
