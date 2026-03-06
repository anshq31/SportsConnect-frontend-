package com.ansh.sportsapp.domain.model

data class UserProfile(
    val id: Long,
    val username: String,
    val experience: String,
    val overallRating: Double,
    val skills: List<String>,
    val reviews: List<Review>
)
