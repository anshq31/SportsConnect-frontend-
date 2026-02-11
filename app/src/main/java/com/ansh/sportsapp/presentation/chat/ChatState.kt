package com.ansh.sportsapp.presentation.chat

import com.ansh.sportsapp.domain.model.ChatMessage

data class ChatState (
    val messages: List<ChatMessage> = emptyList(),
    val messageText: String = ""
)