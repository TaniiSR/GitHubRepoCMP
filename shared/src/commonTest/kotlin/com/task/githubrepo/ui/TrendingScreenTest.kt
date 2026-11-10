package com.task.githubrepo.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import com.task.githubrepo.domain.model.TrendingRepo
import com.task.githubrepo.presentation.trending.TrendingIntent
import com.task.githubrepo.presentation.trending.TrendingState
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class TrendingScreenTest {

    private val repos = listOf(
        TrendingRepo(1, "go", "golang/go", "The Go programming language", "Go", 108415, "golang", null),
        TrendingRepo(2, "kotlin", "JetBrains/kotlin", "Kotlin lang", null, 50000, "JetBrains", null),
    )

    @Test
    fun loadingShowsLoadingView() = runComposeUiTest {
        setContent { TrendingScreen(TrendingState.Loading) {} }
        onNodeWithTag("loadingView").assertIsDisplayed()
    }

    @Test
    fun contentShowsRowsWithNameAndFullName() = runComposeUiTest {
        setContent { TrendingScreen(TrendingState.Content(repos)) {} }
        onNodeWithTag("repoRow_1").assertIsDisplayed()
        onNodeWithTag("repoRow_2").assertIsDisplayed()
        onNodeWithText("golang/go").assertIsDisplayed()
        onNodeWithText("go").assertIsDisplayed()
        onNodeWithText("JetBrains/kotlin").assertIsDisplayed()
    }

    @Test
    fun detailsAreHiddenUntilExpandedAndClickEmitsToggleExpand() = runComposeUiTest {
        val intents = mutableListOf<TrendingIntent>()
        setContent { TrendingScreen(TrendingState.Content(repos)) { intents += it } }

        onNodeWithTag("details_1", useUnmergedTree = true).assertDoesNotExist()
        onNodeWithTag("repoRow_1").performClick()

        assertEquals(listOf<TrendingIntent>(TrendingIntent.ToggleExpand(1)), intents)
    }

    @Test
    fun expandedRowShowsDescriptionLanguageAndStars() = runComposeUiTest {
        setContent { TrendingScreen(TrendingState.Content(repos, expandedIds = setOf(1L, 2L))) {} }

        onNodeWithTag("details_1", useUnmergedTree = true).assertIsDisplayed()
        onNodeWithText("The Go programming language").assertIsDisplayed()
        onNodeWithText("Go").assertIsDisplayed()
        onNodeWithText("108415").assertIsDisplayed()
        // null language falls back to the repo name
        onNodeWithTag("details_2", useUnmergedTree = true).assertIsDisplayed()
        onNodeWithText("50000").assertIsDisplayed()
    }

    @Test
    fun errorShowsTextsAndRetryEmitsRetry() = runComposeUiTest {
        val intents = mutableListOf<TrendingIntent>()
        setContent { TrendingScreen(TrendingState.Error("Not Found")) { intents += it } }

        onNodeWithTag("errorView").assertIsDisplayed()
        onNodeWithText("Something went wrong").assertIsDisplayed()
        onNodeWithText("An alien is probably blocking your signal").assertIsDisplayed()
        onNodeWithText("Not Found").assertIsDisplayed()
        onNodeWithTag("retryButton").performClick()

        assertEquals(listOf<TrendingIntent>(TrendingIntent.Retry), intents)
    }
}
