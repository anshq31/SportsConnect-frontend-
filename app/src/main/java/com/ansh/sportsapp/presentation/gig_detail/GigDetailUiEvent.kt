package com.ansh.sportsapp.presentation.gig_detail

sealed class GigDetailUiEvent {
    object JoinSuccess: GigDetailUiEvent()
    data class ShowSnackBar(val message : String): GigDetailUiEvent()
}