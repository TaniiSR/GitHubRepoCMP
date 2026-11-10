package com.task.githubrepo.presentation.trending

import app.cash.turbine.test
import com.task.githubrepo.domain.model.DataError
import com.task.githubrepo.domain.model.DataResult
import com.task.githubrepo.domain.model.TrendingRepo
import com.task.githubrepo.domain.usecase.FakeTrendingRepository
import com.task.githubrepo.domain.usecase.GetTrendingReposUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class TrendingViewModelTest {
    private val generic = "Request failed please try again"
    private val a = repo(1)
    private val b = repo(2)
    private val c = repo(3)
    private lateinit var fake: FakeTrendingRepository
    private lateinit var vm: TrendingViewModel

    private fun repo(id: Long) = TrendingRepo(id, "n$id", "o/n$id", "d", "Kotlin", 10, "o", null)
    private fun ok(vararg r: TrendingRepo) = DataResult.Success(r.toList())

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        fake = FakeTrendingRepository()
        vm = TrendingViewModel(GetTrendingReposUseCase(fake))
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun initialStateIsLoadingAndNothingFetchedUntilLoad() = runTest {
        assertEquals(TrendingState.Loading, vm.state.value)
        testScheduler.advanceUntilIdle()
        assertEquals(emptyList(), fake.calls)
    }

    @Test
    fun loadEmitsLoadingThenContent() = runTest {
        fake.enqueue(ok(a, b))
        vm.state.test {
            assertEquals(TrendingState.Loading, awaitItem())
            vm.onIntent(TrendingIntent.Load)
            assertEquals(TrendingState.Content(listOf(a, b)), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(listOf(false), fake.calls)
    }

    @Test
    fun loadFailureWithNoContentEmitsError() = runTest {
        fake.enqueue(DataResult.Failure(DataError.NotFound))
        vm.state.test {
            assertEquals(TrendingState.Loading, awaitItem())
            vm.onIntent(TrendingIntent.Load)
            assertEquals(TrendingState.Error("Not Found"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadIsIgnoredWhenAlreadyLoadingOrLoaded() = runTest {
        fake.enqueue(ok(a))
        vm.onIntent(TrendingIntent.Load)
        vm.onIntent(TrendingIntent.Load)
        testScheduler.advanceUntilIdle()
        vm.onIntent(TrendingIntent.Load)
        testScheduler.advanceUntilIdle()
        assertEquals(listOf(false), fake.calls)
        assertEquals(TrendingState.Content(listOf(a)), vm.state.value)
    }

    @Test
    fun emptyListOnLoadIsShownAsError() = runTest {
        fake.enqueue(ok())
        vm.state.test {
            assertEquals(TrendingState.Loading, awaitItem())
            vm.onIntent(TrendingIntent.Load)
            assertEquals(TrendingState.Error(generic), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refreshWhileContentKeepsContentRefreshingThenReplacesIt() = runTest {
        fake.enqueue(ok(a), ok(b, c))
        vm.state.test {
            assertEquals(TrendingState.Loading, awaitItem())
            vm.onIntent(TrendingIntent.Load)
            assertEquals(TrendingState.Content(listOf(a)), awaitItem())
            vm.onIntent(TrendingIntent.ToggleExpand(1))
            assertEquals(TrendingState.Content(listOf(a), expandedIds = setOf(1)), awaitItem())
            vm.onIntent(TrendingIntent.Refresh)
            assertEquals(
                TrendingState.Content(listOf(a), isRefreshing = true, expandedIds = setOf(1)),
                awaitItem(),
            )
            assertEquals(
                TrendingState.Content(listOf(b, c), isRefreshing = false, expandedIds = setOf(1)),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(listOf(false, true), fake.calls)
    }

    @Test
    fun refreshFailureKeepsOldContentAndEmitsMessage() = runTest {
        fake.enqueue(ok(a), DataResult.Failure(DataError.Timeout))
        vm.onIntent(TrendingIntent.Load)
        testScheduler.advanceUntilIdle()
        vm.effects.test {
            vm.state.test {
                assertEquals(TrendingState.Content(listOf(a)), awaitItem())
                vm.onIntent(TrendingIntent.Refresh)
                assertEquals(TrendingState.Content(listOf(a), isRefreshing = true), awaitItem())
                assertEquals(TrendingState.Content(listOf(a), isRefreshing = false), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            assertEquals(
                TrendingEffect.ShowMessage("Please check your internet connection"),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refreshReturningEmptyListWhileContentBecomesError() = runTest {
        fake.enqueue(ok(a), ok())
        vm.onIntent(TrendingIntent.Load)
        testScheduler.advanceUntilIdle()
        vm.onIntent(TrendingIntent.Refresh)
        testScheduler.advanceUntilIdle()
        assertEquals(TrendingState.Error(generic), vm.state.value)
    }

    @Test
    fun retryFromErrorEmitsLoadingThenContentWithForceRefresh() = runTest {
        fake.enqueue(DataResult.Failure(DataError.Unknown), ok(a))
        vm.onIntent(TrendingIntent.Load)
        testScheduler.advanceUntilIdle()
        vm.state.test {
            assertEquals(TrendingState.Error(generic), awaitItem())
            vm.onIntent(TrendingIntent.Retry)
            assertEquals(TrendingState.Loading, awaitItem())
            assertEquals(TrendingState.Content(listOf(a)), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(listOf(false, true), fake.calls)
    }

    @Test
    fun toggleExpandAddsAndRemovesMultipleIds() = runTest {
        fake.enqueue(ok(a, b))
        vm.onIntent(TrendingIntent.Load)
        testScheduler.advanceUntilIdle()
        vm.onIntent(TrendingIntent.ToggleExpand(1))
        vm.onIntent(TrendingIntent.ToggleExpand(2))
        assertEquals(setOf(1L, 2L), (vm.state.value as TrendingState.Content).expandedIds)
        vm.onIntent(TrendingIntent.ToggleExpand(1))
        assertEquals(setOf(2L), (vm.state.value as TrendingState.Content).expandedIds)
    }

    @Test
    fun toggleExpandOutsideContentIsNoOp() = runTest {
        vm.onIntent(TrendingIntent.ToggleExpand(1))
        assertEquals(TrendingState.Loading, vm.state.value)
        fake.enqueue(DataResult.Failure(DataError.Unknown))
        vm.onIntent(TrendingIntent.Load)
        testScheduler.advanceUntilIdle()
        vm.onIntent(TrendingIntent.ToggleExpand(1))
        assertEquals(TrendingState.Error(generic), vm.state.value)
    }

    @Test
    fun refreshWhileRefreshingOrLoadingIsIgnored() = runTest {
        vm.onIntent(TrendingIntent.Refresh) // Loading: ignored
        testScheduler.advanceUntilIdle()
        assertEquals(emptyList(), fake.calls)

        fake.enqueue(ok(a), ok(b))
        vm.onIntent(TrendingIntent.Load)
        testScheduler.advanceUntilIdle()
        vm.onIntent(TrendingIntent.Refresh)
        vm.onIntent(TrendingIntent.Refresh) // already refreshing: ignored
        testScheduler.advanceUntilIdle()
        assertEquals(listOf(false, true), fake.calls)
    }
}
