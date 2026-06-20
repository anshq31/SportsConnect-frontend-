package com.ansh.sportsapp.domain.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    val events: SharedFlow<Long> = _events.asSharedFlow()

    suspend fun emit(blockedUserId: Long) = _events.emit(blockedUserId)
}
