package com.ansh.sportsapp.presentation.edit_user

sealed class EditProfileUiEvent {
    object SaveSuccess : EditProfileUiEvent()
    data class ShowSnackbar(val message: String) : EditProfileUiEvent()
}