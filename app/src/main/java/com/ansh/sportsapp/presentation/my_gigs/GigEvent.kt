package com.ansh.sportsapp.presentation.my_gigs

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class GigEvent{
    object GigCreated : GigEvent()
    object GigCompleted : GigEvent()
    object GigJoined : GigEvent()
}


@Singleton
class GigEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<GigEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    suspend fun emit(event: GigEvent) {
        _events.emit(event)
    }
}