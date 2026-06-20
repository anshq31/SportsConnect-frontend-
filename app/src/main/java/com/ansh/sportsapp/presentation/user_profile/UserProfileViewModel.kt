package com.ansh.sportsapp.presentation.user_profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.data.local.AuthPreferences
import com.ansh.sportsapp.domain.event.BlockEventBus
import com.ansh.sportsapp.domain.usecase.review.GetReviewUseCase
import com.ansh.sportsapp.domain.usecase.user.GetUserProfileUseCase
import com.ansh.sportsapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getReviewUseCase: GetReviewUseCase,
    private val userRepository: UserRepository,
    private val authPreferences: AuthPreferences,
    private val blockEventBus: BlockEventBus,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(UserProfileState())
    val state: StateFlow<UserProfileState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UserProfileUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val userId: Long = savedStateHandle.get<Long>("userId") ?: -1L

    init {
        if (userId != -1L) {
            loadProfile()
            checkBlockStatus()
        }
    }

    private fun checkBlockStatus() {
        viewModelScope.launch {
            val blocked = authPreferences.blockedUserIds.first()
            _state.update { it.copy(isBlockedByMe = userId in blocked) }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, isBlockedAccess = false) }
            when (val result = getUserProfileUseCase(userId)) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, profile = result.data) }
                    loadReviews()
                }
                is Resource.Error -> {
                    if (result.message == "403") {
                        _state.update { it.copy(isLoading = false, isBlockedAccess = true) }
                    } else {
                        _state.update { it.copy(isLoading = false, error = result.message ?: "Failed to load profile") }
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun loadReviews() {
        viewModelScope.launch {
            _state.update { it.copy(isReviewLoading = true) }
            when (val result = getReviewUseCase(userId)) {
                is Resource.Success -> _state.update { it.copy(isReviewLoading = false, reviews = result.data ?: emptyList()) }
                is Resource.Error -> _state.update { it.copy(isReviewLoading = false, reviewsError = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun blockUser() {
        viewModelScope.launch {
            _state.update { it.copy(isBlockLoading = true) }
            when (val result = userRepository.blockUser(userId, state.value.profile?.username ?: "")) {
                is Resource.Success -> {
                    blockEventBus.emit(userId)
                    _state.update { it.copy(isBlockedByMe = true, isBlockLoading = false) }
                    _uiEvent.emit(UserProfileUiEvent.ShowSnackbar("User blocked"))
                }
                is Resource.Error -> {
                    _state.update { it.copy(isBlockLoading = false) }
                    _uiEvent.emit(UserProfileUiEvent.ShowSnackbar(result.message ?: "Failed to block user"))
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun unblockUser() {
        viewModelScope.launch {
            _state.update { it.copy(isBlockLoading = true) }
            when (val result = userRepository.unblockUser(userId)) {
                is Resource.Success -> {
                    blockEventBus.emit(userId)
                    _state.update { it.copy(isBlockedByMe = false, isBlockLoading = false) }
                    _uiEvent.emit(UserProfileUiEvent.ShowSnackbar("User unblocked"))
                }
                is Resource.Error -> {
                    _state.update { it.copy(isBlockLoading = false) }
                    _uiEvent.emit(UserProfileUiEvent.ShowSnackbar(result.message ?: "Failed to unblock user"))
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun refresh() {
        loadProfile()
    }
}
