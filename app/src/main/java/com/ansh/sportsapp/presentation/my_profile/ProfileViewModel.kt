package com.ansh.sportsapp.presentation.my_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.data.local.AuthPreferences
import com.ansh.sportsapp.domain.usecase.user.GetMyProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val authPreferences: AuthPreferences
) : ViewModel(){
    private val _state = MutableStateFlow(ProfileState())
    val state : StateFlow<ProfileState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ProfileUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadProfile()
    }

    fun loadProfile(){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when(val result = getMyProfileUseCase()){
                is Resource.Success->{
                    _state.update { it.copy(profile = result.data, isLoading = false) }
                }
                is Resource.Error->{
                    _state.update { it.copy(isLoading = false, error = result.message ?: "Failed to load profile") }
                }
                is Resource.Loading-> Unit
            }
        }
    }

    fun logOut(){
        viewModelScope.launch {
            authPreferences.clearAuthData()
            _uiEvent.emit(ProfileUiEvent.LoggedOut)
        }
    }

    fun refresh(){
        loadProfile()
    }
}