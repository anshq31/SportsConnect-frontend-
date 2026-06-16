package com.ansh.sportsapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ansh.sportsapp.data.local.database.chat.ChatMessageDao
import com.ansh.sportsapp.data.local.database.chat.ChatMessageEntity

@Database(
    entities = [ChatMessageEntity::class],
    version = 2,
    exportSchema = false
)
abstract class SportsDatabase : RoomDatabase(){
    abstract fun chatMessageDao(): ChatMessageDao
}