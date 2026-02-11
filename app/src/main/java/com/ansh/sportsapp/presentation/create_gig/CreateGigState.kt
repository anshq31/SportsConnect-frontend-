package com.ansh.sportsapp.presentation.create_gig

data class CreateGigState (
    val sport: String = "",
    val location: String = "",
    val date: String = "", // YYYY-MM-DD
    val time: String = "", // HH:MM
    val players: String = "",
    val isLoading: Boolean = false
)