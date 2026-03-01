package com.ansh.sportsapp.domain.model

data class Gig(
    val id: Long,
    val sport: String,
    val location: String,
    val dateTime: String,
    val playersNeeded: Int,
    val gigMasterUsername: String,
    val isOwner : Boolean,
    val isParticipant : Boolean,
    val requestStatus : String = "NONE"
)