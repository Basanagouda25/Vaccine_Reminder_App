package com.basu.vaccineremainder.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * A simple singleton object to send one-off events between different parts
 * of the app, like from a LoginScreen to a ChildViewModel, telling it to refresh.
 */
object RefreshManager {

    private val _refreshFlow = MutableSharedFlow<Unit>()
    val refreshFlow = _refreshFlow.asSharedFlow()

    suspend fun triggerRefresh() {
        _refreshFlow.emit(Unit)
    }
}
