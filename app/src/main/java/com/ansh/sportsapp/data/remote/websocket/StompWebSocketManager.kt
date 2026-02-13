package com.ansh.sportsapp.data.remote.websocket

import android.util.Log
import com.ansh.sportsapp.data.local.AuthPreferences
import com.ansh.sportsapp.data.remote.dto.chat.ChatMessageDto
import com.ansh.sportsapp.domain.model.ChatMessage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StompWebSocketManager @Inject constructor(
    private val client : OkHttpClient,
    private val authPreferences: AuthPreferences,
    private val gson: Gson
): WebSocketListener(){

    private var webSocket: WebSocket? = null
    private val _messageFlow = MutableSharedFlow<ChatMessage>(
        replay = 1
    )
    val messageFlow : Flow<ChatMessage> = _messageFlow.asSharedFlow()

    private val scope = CoroutineScope(SupervisorJob()+ Dispatchers.IO)

    private var currentUsername : String? = null

    private val BASE_WS_URL = "ws://10.57.43.168:8080/ws"

    private var currentGroupId : Long = -1

    fun connect(groupId : Long){

        currentGroupId = groupId

        scope.launch {

            val token = runBlocking { authPreferences.accessToken.first() }?:return@launch

            currentUsername = authPreferences.username.first()?:""

            val request = Request.Builder().url(BASE_WS_URL).build()

            webSocket = client.newWebSocket(request,this@StompWebSocketManager)

            // 1. Send CONNECT
            // STOMP requires a NULL character (\u0000) at the end of every frame
            val connectFrame = "CONNECT\naccept-version:1.2\nhost:10.195.225.168\nAuthorization:Bearer $token\n\n\u0000"
            sendStompFrame(connectFrame)

        }
    }

    private fun sendStompFrame(frame: String) {
        Log.d("STOMP_OUT", frame.replace("\u0000", "[NULL]"))
        webSocket?.send(frame)
    }

    fun sendMessage(groupId: Long,content: String){
        val jsonContent = gson.toJson(mapOf("content" to content))
        val sendFrame = "SEND\ndestination:/app/chat/$groupId/send\ncontent-type:application/json\ncontent-length:${jsonContent.length}\n\n$jsonContent\u0000"
        sendStompFrame(sendFrame)
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("STOMP_IN", text)
        if (text.startsWith("MESSAGE")) {
            try {
                val bodyStartIndex = text.indexOf("\n\n")
                if (bodyStartIndex != -1) {
                    val body = text.substring(bodyStartIndex+2).trim().replace("\u0000", "")

                    if (body.isNotEmpty()){
                        val dto = gson.fromJson(body, ChatMessageDto::class.java)


                        val domainMessage = ChatMessage(
                            senderUsername = dto.senderUsername,
                            content = dto.content,
                            timeStamp = dto.timeStamp,
                            isFromMe = dto.senderUsername == currentUsername
                        )

                        _messageFlow.tryEmit(domainMessage)
                        Log.d("STOMP_SUCCESS", "Message emitted to UI: ${dto.content}")
                    }
                }
            } catch (e: Exception) {
                Log.e("STOMP", "Failed to parse message: ${e.message}")
            }
        } else if (text.startsWith("CONNECTED")) {
            Log.d("STOMP", "Connected to Server!")

            // 2. Send SUBSCRIBE
            val subscribeFrame = "SUBSCRIBE\nid:sub-0\ndestination:/topic/chat/$currentGroupId\nack:auto\n\n\u0000"
            sendStompFrame(subscribeFrame)
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("STOMP_STATUS", "WebSocket Opened")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("STOMP_FAIL", "Connection failure: ${t.message}. Response: ${response?.code}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("STOMP_STATUS", "WebSocket Closing: $reason")
    }

}