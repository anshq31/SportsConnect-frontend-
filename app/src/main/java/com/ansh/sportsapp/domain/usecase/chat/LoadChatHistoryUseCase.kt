package com.ansh.sportsapp.domain.usecase.chat

import com.ansh.sportsapp.domain.repository.ChatRepository
import javax.inject.Inject

class LoadChatHistoryUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(groupId : Long){
        repository.loadHistory(groupId)
    }
}