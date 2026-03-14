package com.ansh.sportsapp.data.remote.dto.user

data class ReviewRequestDto (
    val gigId : Long,
    val participantId : Long,
    val rating : Int,
    val comment : String
)