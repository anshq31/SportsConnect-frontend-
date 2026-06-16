package com.ansh.sportsapp.data.remote.dto.chat

data class ChatMessageDto (
    val id : String,
    val senderId: Long = 0,
    val senderUsername: String,
    val content : String,
    val timeStamp : String
)