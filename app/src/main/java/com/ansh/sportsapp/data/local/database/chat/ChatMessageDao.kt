package com.ansh.sportsapp.data.local.database.chat

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ansh.sportsapp.data.local.database.chat.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Query("""
        SELECT * FROM chat_messages
        WHERE groupId = :groupId
        ORDER BY timeStamp ASC
    """
    )
    fun observeMessages (groupId: Long): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(messages : List<ChatMessageEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(message: ChatMessageEntity)
}