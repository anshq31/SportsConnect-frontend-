package com.ansh.sportsapp.data.repository

import com.ansh.sportsapp.data.local.database.chat.ChatMessageDao
import com.ansh.sportsapp.data.local.database.chat.ChatMessageEntity
import com.ansh.sportsapp.data.remote.SportsApi
import com.ansh.sportsapp.data.remote.websocket.StompWebSocketManager
import com.ansh.sportsapp.domain.model.ChatMessage
import com.ansh.sportsapp.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import okhttp3.Dispatcher
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val api : SportsApi,
    private val dao : ChatMessageDao,
    private val webSocketManager: StompWebSocketManager
): ChatRepository{

    private var currentGroupId : Long = -1

    init {
        webSocketManager.messageFlow
            .onEach { message ->
                if (currentGroupId!= (-1).toLong()){
                    dao.insert(
                        ChatMessageEntity(
                            id = "${message.senderUsername}-${message.timeStamp}",
                            groupId = currentGroupId,
                            senderUsername = message.senderUsername,
                            content = message.content,
                            timeStamp = message.timeStamp
                        )
                    )
                }
            }
            .launchIn(CoroutineScope(Dispatchers.IO))
    }

    override fun connectToChat(groupId: Long) {
        currentGroupId = groupId
        webSocketManager.connect(groupId)
    }

    override fun disconnectFromChat() {
        webSocketManager.disconnect()
    }

    override fun sendMessage(groupId: Long, content: String) {
        webSocketManager.sendMessage(groupId,content)
    }

    override fun observeMessages(groupId: Long): Flow<List<ChatMessage>> =
        dao.observeMessages(groupId).map {entities->
            entities.map {
                ChatMessage(
                    senderUsername = it.senderUsername,
                    content = it.content,
                    timeStamp = it.timeStamp,
                    isFromMe = false
                )
            }
        }


    override suspend fun loadHistory(groupId: Long) {
        val page = api.getChatHistory(groupId)
        dao.insertAll(
            page.content.map {
                ChatMessageEntity(
                    id = "${it.senderUsername}-${it.timeStamp}",
                    groupId = groupId,
                    senderUsername = it.senderUsername,
                    content = it.content,
                    timeStamp = it.timeStamp
                )
            }
        )
    }

}