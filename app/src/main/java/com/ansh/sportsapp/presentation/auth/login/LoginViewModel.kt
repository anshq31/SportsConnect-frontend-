package com.ansh.sportsapp.presentation.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.usecase.auth.LoginUseCase
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
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel(){
    private val _state = MutableStateFlow(LoginState())
    val state : StateFlow<LoginState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<LoginUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        Log.d("LOGIN_VM", "LoginViewModel CREATED: ${this.hashCode()}")
    }

    fun onEvent(event: LoginEvent){
        Log.d("LOGIN_VM", "Event received: $event | vm=${this.hashCode()}")
        when(event){
            is LoginEvent.EnteredUsername ->{
                _state.update { it.copy(username = event.value) }
                println("Event received: EnteredUsername(value=${event.value}) | vm=${hashCode()}")
            }

            is LoginEvent.EnteredPassword -> {
                _state.update { it.copy(password = event.value) }
                println("Event received: EnteredPassword(value=${event.value}) | vm=${hashCode()}")
            }

            is LoginEvent.Login -> {
                println("Event received: Login | vm=${hashCode()}")
                login()
            }
        }
    }

    private fun login(){
        val username = state.value.username
        val password = state.value.password

        viewModelScope.launch {
            Log.d("LOGIN_VM","Button Clicked")


            _state.update { it.copy(isLoading = true, error = null) }

            val result = loginUseCase(username,password)


            Log.d("LOGIN_VM", "loginUseCase returned: $result")

            when(result){
                is Resource.Success ->{
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                        )
                    }

                    _uiEvent.emit(
                        LoginUiEvent.NavigateHome(
                            userId = result.data!!.id,
                            accessToken = result.data.accessToken
                        )
                    )
                    Log.d("LOGIN_VM", "Login SUCCESS: ${result.data}")
                }

                is Resource.Error-> {
                    _state.update { it.copy(
                        isLoading = false,
                        error = result.message ?: "Unknown Error"
                    ) }

                    _uiEvent.emit(
                        LoginUiEvent.ShowSnackBar(
                            result.message ?: "Login failed"
                        )
                    )
                    Log.d("LOGIN_VM", "Login ERROR: ${result.message}")
                }
                is Resource.Loading-> Unit
            }
        }


    }

}