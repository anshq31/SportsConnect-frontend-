package com.ansh.sportsapp.presentation.blocked_users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.data.local.AuthPreferences
import com.ansh.sportsapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockedUsersViewModel @Inject constructor(
    private val authPreferences: AuthPreferences,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BlockedUsersState())
    val state = _state.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            authPreferences.blockedUserEntries.collectLatest { entries ->
                _state.update { it.copy(blockedUsers = entries) }
            }
        }
    }


    fun unblockUser(userId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(unblockingId = userId) }
            when (val result = userRepository.unblockUser(userId)) {
                is Resource.Success -> _toastEvent.emit("User unblocked")
                is Resource.Error -> _toastEvent.emit(result.message ?: "Failed to unblock")
                is Resource.Loading -> Unit
            }
            _state.update { it.copy(unblockingId = null) }
        }
    }
}
