package com.ansh.sportsapp.presentation.user_profile

sealed class UserProfileUiEvent {
    data class ShowSnackbar(val message: String) : UserProfileUiEvent()
}
