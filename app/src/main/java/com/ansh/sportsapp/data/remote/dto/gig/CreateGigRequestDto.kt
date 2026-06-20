package com.ansh.sportsapp.data.remote.dto.gig

data class CreateGigRequestDto (
    val sport: String,
    val location: String,
    val latitude: Double?,
    val longitude: Double?,
    val dateTime: String,
    val playersNeeded: Int
)