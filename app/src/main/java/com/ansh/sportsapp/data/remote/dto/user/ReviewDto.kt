package com.ansh.sportsapp.data.remote.dto.user

data class ReviewDto (
    val id: Long,
    val gigId: Long,
    val reviewerUsername: String,
    val rating: Int,
    val comment: String?
)