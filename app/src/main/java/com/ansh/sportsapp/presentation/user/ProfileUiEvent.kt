package com.ansh.sportsapp.presentation.user

sealed class ProfileUiEvent {
    object LoggedOut : ProfileUiEvent()
}