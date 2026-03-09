package com.ansh.sportsapp.data.repository

import android.util.Log
import com.ansh.sportsapp.data.local.AuthPreferences
import com.ansh.sportsapp.data.local.database.chat.ChatMessageDao
import com.ansh.sportsapp.data.local.database.chat.ChatMessageEntity
import com.ansh.sportsapp.data.remote.SportsApi
import com.ansh.sportsapp.data.remote.websocket.StompWebSocketManager
import com.ansh.sportsapp.domain.model.ChatMessage
import com.ansh.sportsapp.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val api : SportsApi,
    private val dao : ChatMessageDao,
    private val webSocketManager: StompWebSocketManager,
    private val authPreferences: AuthPreferences
): ChatRepository{

    private var currentGroupId : Long = -1

    private val scope = CoroutineScope(SupervisorJob()+ Dispatchers.IO)

    init {
        webSocketManager.messageFlow
            .onEach { message ->
                val groupId = currentGroupId
                if (groupId!= (-1).toLong()){
                    dao.insert(
                        ChatMessageEntity(
                            id = message.id,
                            groupId = groupId,
                            senderUsername = message.senderUsername,
                            content = message.content,
                            timeStamp = message.timeStamp
                        )
                    )
                }
            }
            .launchIn(scope)
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
        dao.observeMessages(groupId).map { entities->
            val currentUsername = authPreferences.username.firstOrNull()?:""
            entities.map {
                ChatMessage(
                    id = it.id,
                    senderUsername = it.senderUsername,
                    content = it.content,
                    timeStamp = it.timeStamp,
                    isFromMe = it.senderUsername == currentUsername
                )
            }
        }.distinctUntilChanged()


    override suspend fun loadHistory(groupId: Long) {
        withContext(Dispatchers.IO){
            val page = api.getChatHistory(groupId)
            dao.insertAll(
                page.content.map {
                    ChatMessageEntity(
                        id = it.id,
                        groupId = groupId,
                        senderUsername = it.senderUsername,
                        content = it.content,
                        timeStamp = it.timeStamp
                    )
                }
            )
        }
    }

}