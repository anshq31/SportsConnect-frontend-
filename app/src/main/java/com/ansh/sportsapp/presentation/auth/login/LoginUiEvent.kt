package com.ansh.sportsapp.presentation.auth.login

sealed class LoginUiEvent {
    data class NavigateHome(
        val userId: Long,
        val accessToken : String
    ): LoginUiEvent()

    data class ShowSnackBar(val message : String): LoginUiEvent()
}