package com.ansh.sportsapp.data.remote.dto.user

import java.math.BigDecimal

data class UserProfileDto(
    val id : Long,
    val username : String,
    val experience : String?,
    val overallRating : Double?,
    val skill : Set<String>?,
    val reviewsReceived : List<ReviewDto>?
)