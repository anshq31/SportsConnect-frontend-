package com.ansh.sportsapp.domain.usecase.chat

import com.ansh.sportsapp.domain.repository.ChatRepository
import javax.inject.Inject

class DisconnectChatUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(){
        repository.disconnectFromChat()
    }
}