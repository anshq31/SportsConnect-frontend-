package com.ansh.sportsapp.domain.usecase.chat

import com.ansh.sportsapp.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(groupId: Long, content : String){
        if (content.isNotBlank()) {
            repository.sendMessage(groupId, content)
        }
    }
}