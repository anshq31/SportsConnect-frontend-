package com.ansh.sportsapp.domain.model

data class Review(
    val id: Long,
    val gigId: Long,
    val reviewerUsername: String,
    val rating: Int,
    val comment: String
)
