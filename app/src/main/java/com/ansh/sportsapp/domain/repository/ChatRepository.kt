package com.ansh.sportsapp.domain.repository

import com.ansh.sportsapp.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
        fun connectToChat(groupId: Long)
        fun disconnectFromChat()
        fun sendMessage(groupId: Long, content: String)
        fun observeMessages(groupId: Long): Flow<List<ChatMessage>>
        suspend fun loadHistory(groupId: Long)
}