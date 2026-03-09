package com.ansh.sportsapp.presentation.user_profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.data.local.AuthPreferences
import com.ansh.sportsapp.domain.usecase.user.GetMyProfileUseCase
import com.ansh.sportsapp.domain.usecase.user.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val savedStateHandle: SavedStateHandle
): ViewModel(){
    private val _state = MutableStateFlow(UserProfileState())
    val state: StateFlow<UserProfileState> = _state.asStateFlow()

    private val userId: Long = savedStateHandle.get<Long>("userId") ?: -1L

    init {
        if (userId != -1L) {
            loadProfile()
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = getUserProfileUseCase(userId)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(isLoading = false, profile = result.data)
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Failed to load profile"
                        )
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun refresh() {
        loadProfile()
    }
}