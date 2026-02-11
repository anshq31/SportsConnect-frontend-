package com.ansh.sportsapp.data.remote.dto.chat

data class ChatMessageDto (
    val senderUsername: String,
    val content : String,
    val timeStamp : String
)