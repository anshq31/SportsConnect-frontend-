package com.ansh.sportsapp.presentation.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.data.local.AuthPreferences
import com.ansh.sportsapp.domain.model.ChatMessage
import com.ansh.sportsapp.domain.usecase.chat.ConnectChatUseCase
import com.ansh.sportsapp.domain.usecase.chat.DisconnectChatUseCase
import com.ansh.sportsapp.domain.usecase.chat.LoadChatHistoryUseCase
import com.ansh.sportsapp.domain.usecase.chat.ObserveMessageUseCase
import com.ansh.sportsapp.domain.usecase.chat.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val connectChatUseCase: ConnectChatUseCase,
    private val disconnectChatUseCase: DisconnectChatUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val observeMessagesUseCase: ObserveMessageUseCase,
    private val loadChatHistoryUseCase : LoadChatHistoryUseCase,
    savedStateHandle: SavedStateHandle,
    authPreferences: AuthPreferences
) : ViewModel(){
    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    private val gigId: Long = savedStateHandle.get<Long>("gigId")?: -1L

    private val currentUser = MutableStateFlow("Unknown")

    init {
        viewModelScope.launch {
            currentUser.value = authPreferences.username.firstOrNull()?:"Unknown"

            try {
                loadChatHistoryUseCase(gigId)
            }catch (e : Exception){
                Log.e("ChatVM", "History load failed: ${e.message}")
            }

            connectChatUseCase(gigId)
        }

        observeMessagesUseCase(gigId)
            .onEach { messages ->
                _state.update {
                    it.copy(messages = messages.map { msg->
                        msg.copy(isFromMe = msg.senderUsername == currentUser.value)
                    })
                }
            }.launchIn(viewModelScope)
    }

//    fun connectToChat(){
//        connectChatUseCase(gigId)
//
//        observeMessagesUseCase()
//            .onEach { message ->
//                _state.update {it.copy(messages = it.messages + message.copy(isFromMe = (message.senderUsername == currentUser.value)))}
//            }
//            .launchIn(viewModelScope)
//    }

    fun onMessageChange(text: String){
        _state.update { it.copy(messageText = text) }
    }

    fun sendMessage(){
        val text = state.value.messageText
        if (text.isNotBlank()){
            sendMessageUseCase(gigId,text)
            _state.update { it.copy(messageText = "")}
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnectChatUseCase()
    }
}