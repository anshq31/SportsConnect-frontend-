package com.ansh.sportsapp.domain.model

data class ChatMessage(
    val id : String,
    val senderUsername : String,
    val content : String,
    val timeStamp : String,
    val isFromMe : Boolean
)
