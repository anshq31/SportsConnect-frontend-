package com.ansh.sportsapp.data.local.database.chat

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("chat_messages")
data class ChatMessageEntity (
    @PrimaryKey
    val id : String,
    val groupId : Long,
    val senderUsername : String,
    val content : String,
    val timeStamp : String
)