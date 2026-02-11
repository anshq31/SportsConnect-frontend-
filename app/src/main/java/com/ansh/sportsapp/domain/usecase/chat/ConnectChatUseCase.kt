package com.ansh.sportsapp.domain.usecase.chat

import com.ansh.sportsapp.domain.repository.ChatRepository
import javax.inject.Inject

class ConnectChatUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(groupId: Long){
        repository.connectToChat(groupId)
    }
}