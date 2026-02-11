package com.ansh.sportsapp.presentation.create_gig

sealed class CreateGigEvent{
    data class EnteredSport(val value: String) : CreateGigEvent()
    data class EnteredLocation(val value: String) : CreateGigEvent()
    data class EnteredDate(val value: String) : CreateGigEvent()
    data class EnteredTime(val value: String) : CreateGigEvent()
    data class EnteredPlayers(val value: String) : CreateGigEvent()
    object Submit : CreateGigEvent()
}