package com.ansh.sportsapp.presentation.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.usecase.auth.RegisterUseCase
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
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
): ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state : StateFlow<RegisterState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<RegisterUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.EnteredUsername -> {
                _state.update { it.copy(username = event.value) }
            }
            is RegisterEvent.EnteredEmail -> {
                _state.update { it.copy(email = event.value) }
            }
            is RegisterEvent.EnteredPassword -> {
                _state.update { it.copy(password = event.value) }
            }
            is RegisterEvent.Register -> {
                register()
            }
        }
    }

    private fun register() {

        if (state.value.isLoading) return

        val username = state.value.username
        val email = state.value.email
        val password = state.value.password

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = registerUseCase(username, email, password)

            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _uiEvent.emit(RegisterUiEvent.RegistrationSuccess)
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                    _uiEvent.emit(RegisterUiEvent.ShowSnackBar(result.message ?: "Registration failed"))
                }
                is Resource.Loading -> Unit
            }
        }
    }
}