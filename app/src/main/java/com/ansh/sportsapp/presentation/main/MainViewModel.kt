package com.ansh.sportsapp.presentation.main

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.data.local.AuthPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authPreferences: AuthPreferences
) : ViewModel(){
    private val _isAuthChecked = MutableStateFlow(false)
    val isAuthChecked : StateFlow<Boolean> = _isAuthChecked.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = authPreferences.accessToken
        .map {token->
            _isAuthChecked.value = true
            token != null
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            false
        )

}