package com.ansh.sportsapp.presentation.create_gig

sealed class CreateGigUiEvent {
    object GigCreated : CreateGigUiEvent()
    data class ShowSnackbar(val message: String) : CreateGigUiEvent()
}