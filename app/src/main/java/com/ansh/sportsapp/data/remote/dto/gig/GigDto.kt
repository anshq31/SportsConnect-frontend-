package com.ansh.sportsapp.data.remote.dto.gig

import com.google.gson.annotations.SerializedName

data class GigDto(
    val id: Long,
    val sport: String,
    val location: String,
    val dateTime: String, // ISO String (e.g. "2026-05-10T18:00:00")
    val playersNeeded: Int,
    val status: String,
    val gigMasterUsername: String,
    val acceptedParticipantUsernames: List<String>,
    @SerializedName("isOwner")
    val isOwner: Boolean,
    @SerializedName("isParticipant")
    val isParticipant: Boolean,
    val requestStatus : String = "NONE"
)