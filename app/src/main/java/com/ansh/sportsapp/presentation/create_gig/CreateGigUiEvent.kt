package com.ansh.sportsapp.presentation.create_gig

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Singleton

sealed class CreateGigUiEvent {
    object GigCreated : CreateGigUiEvent()
    data class ShowSnackbar(val message: String) : CreateGigUiEvent()
}

