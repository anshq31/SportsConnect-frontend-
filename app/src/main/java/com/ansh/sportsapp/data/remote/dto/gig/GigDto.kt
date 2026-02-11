package com.ansh.sportsapp.data.remote.dto.gig

data class GigDto(
    val id: Long,
    val sport: String,
    val location: String,
    val dateTime: String, // ISO String (e.g. "2026-05-10T18:00:00")
    val playersNeeded: Int,
    val status: String,
    val gigMasterUsername: String,
    val acceptedParticipantUsernames: List<String>
)