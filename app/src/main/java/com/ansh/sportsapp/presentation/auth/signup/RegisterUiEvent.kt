package com.ansh.sportsapp.presentation.auth.signup

sealed class RegisterUiEvent {
    object RegistrationSuccess: RegisterUiEvent()
    data class ShowSnackBar(val message : String): RegisterUiEvent()
}