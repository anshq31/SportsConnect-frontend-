package com.ansh.sportsapp.presentation.my_profile

sealed class ProfileUiEvent {
    object LoggedOut : ProfileUiEvent()
}