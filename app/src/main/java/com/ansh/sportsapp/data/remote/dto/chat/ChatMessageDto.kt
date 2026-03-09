package com.ansh.sportsapp.data.remote.dto.chat

data class ChatMessageDto (
    val id : String,
    val senderUsername: String,
    val content : String,
    val timeStamp : String
)