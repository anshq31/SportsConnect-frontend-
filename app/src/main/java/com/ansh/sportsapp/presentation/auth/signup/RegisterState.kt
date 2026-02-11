package com.ansh.sportsapp.presentation.auth.signup

data class RegisterState(
    val username : String="",
    val email : String ="",
    val password : String = "",
    val isLoading : Boolean = false,
    val error : String? = null
)