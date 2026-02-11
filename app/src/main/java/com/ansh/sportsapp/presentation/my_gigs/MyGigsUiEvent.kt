package com.ansh.sportsapp.presentation.my_gigs

sealed class MyGigsUiEvent {
    data class ShowSnackbar(val message: String) : MyGigsUiEvent()
}