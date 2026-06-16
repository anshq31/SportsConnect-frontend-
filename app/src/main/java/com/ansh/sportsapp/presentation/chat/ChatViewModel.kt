package com.ansh.sportsapp.presentation.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.BuildConfig
import com.ansh.sportsapp.data.local.AuthPreferences
import com.ansh.sportsapp.domain.repository.ChatRepository
import com.ansh.sportsapp.domain.repository.UserRepository
import com.ansh.sportsapp.domain.usecase.chat.ConnectChatUseCase
import com.ansh.sportsapp.domain.usecase.chat.DisconnectChatUseCase
import com.ansh.sportsapp.domain.usecase.chat.LoadChatHistoryUseCase
import com.ansh.sportsapp.domain.usecase.chat.ObserveMessageUseCase
import com.ansh.sportsapp.domain.usecase.chat.SendMessageUseCase
import com.ansh.sportsapp.common.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val loadChatHistoryUseCase: LoadChatHistoryUseCase,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle,
    private val authPreferences: AuthPreferences
) : ViewModel() {
    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    private val gigId: Long = savedStateHandle.get<Long>("gigId") ?: -1L
    private val currentUser = MutableStateFlow("Unknown")

    init {
        viewModelScope.launch {
            currentUser.value = authPreferences.username.firstOrNull() ?: "Unknown"

            // Load blocked users from local preferences
            val blocked = authPreferences.blockedUserIds.first()
            _state.update { it.copy(blockedUserIds = blocked) }

            try {
                loadChatHistoryUseCase(gigId)
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) Log.e("ChatVM", "History load failed: ${e.message}")
            }

            connectChatUseCase(gigId)
        }

        observeMessagesUseCase(gigId)
            .onEach { messages ->
                val blocked = _state.value.blockedUserIds
                _state.update {
                    it.copy(messages = messages
                        .filter { msg -> msg.senderId !in blocked }
                        .map { msg -> msg.copy(isFromMe = msg.senderUsername == currentUser.value) }
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun onMessageChange(text: String) {
        _state.update { it.copy(messageText = text) }
    }

    fun sendMessage() {
        val text = state.value.messageText
        if (text.isNotBlank()) {
            sendMessageUseCase(gigId, text)
            _state.update { it.copy(messageText = "") }
        }
    }

    fun reportMessage(messageId: String) {
        viewModelScope.launch {
            val result = chatRepository.reportMessage(messageId)
            result.fold(
                onSuccess = { _toastEvent.emit("Message reported") },
                onFailure = { e ->
                    val msg = if (e.message == "already_reported") "Already reported" else "Failed to report"
                    _toastEvent.emit(msg)
                }
            )
        }
    }

    fun blockUser(userId: Long) {
        viewModelScope.launch {
            when (userRepository.blockUser(userId)) {
                is Resource.Success -> {
                    authPreferences.addBlockedUserId(userId)
                    _state.update { current ->
                        val newBlocked = current.blockedUserIds + userId
                        current.copy(
                            blockedUserIds = newBlocked,
                            messages = current.messages.filter { it.senderId !in newBlocked }
                        )
                    }
                    _toastEvent.emit("User blocked")
                }
                is Resource.Error -> _toastEvent.emit("Failed to block user")
                is Resource.Loading -> Unit
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnectChatUseCase()
    }
}
