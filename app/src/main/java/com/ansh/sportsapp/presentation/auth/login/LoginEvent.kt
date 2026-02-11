package com.ansh.sportsapp.presentation.auth.login

sealed class LoginEvent{
    data class EnteredUsername(val value : String) : LoginEvent()
    data class EnteredPassword(val value: String): LoginEvent()
    object Login : LoginEvent()
}