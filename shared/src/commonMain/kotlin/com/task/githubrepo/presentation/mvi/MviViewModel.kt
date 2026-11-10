package com.task.githubrepo.presentation.mvi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

/**
 * Base for MVI view models: the view sends intents via [onIntent], observes [state] and consumes
 * one-shot [effects]. Only subclasses write state.
 */
abstract class MviViewModel<S, I, E>(initialState: S) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effects = Channel<E>(Channel.BUFFERED)
    val effects: Flow<E> = _effects.receiveAsFlow()

    abstract fun onIntent(intent: I)

    protected fun setState(reduce: S.() -> S) = _state.update { it.reduce() }

    protected fun sendEffect(effect: E) {
        _effects.trySend(effect)
    }
}
