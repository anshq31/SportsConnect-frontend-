package com.ansh.sportsapp.data.remote.dto.gig

data class CreateGigRequestDto (
    val sport: String,
    val location: String,
    val dateTime: String, // Format: "2026-05-10T18:00:00"
    val playersNeeded: Int
)