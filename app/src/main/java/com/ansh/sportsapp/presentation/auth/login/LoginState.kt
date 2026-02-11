package com.ansh.sportsapp.presentation.auth.login

data class LoginState(
    val username : String = "" ,
    val password : String = "",
    val isLoading : Boolean = true,
    val error : String? = null,
)