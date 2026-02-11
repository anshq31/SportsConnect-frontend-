package com.ansh.sportsapp.domain.usecase.chat

import com.ansh.sportsapp.domain.model.ChatMessage
import com.ansh.sportsapp.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMessageUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(groupId : Long): Flow<List<ChatMessage>> {
        return repository.observeMessages(groupId)
    }
}