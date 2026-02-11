package com.ansh.sportsapp.presentation.auth.signup

sealed class RegisterEvent {
    data class EnteredUsername(val value : String): RegisterEvent()
    data class EnteredPassword(val value: String): RegisterEvent()
    data class EnteredEmail(val value : String): RegisterEvent()
    object Register: RegisterEvent()
}