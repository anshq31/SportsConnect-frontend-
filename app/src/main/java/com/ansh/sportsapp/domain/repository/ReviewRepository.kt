package com.ansh.sportsapp.domain.repository

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.data.remote.dto.user.ReviewRequestDto
import com.ansh.sportsapp.domain.model.Review

interface ReviewRepository {
    suspend fun submitReview(request : ReviewRequestDto): Resource<Unit>
    suspend fun getReviewsForUser(userId: Long, page : Int = 0, size : Int = 10): Resource<List<Review>>
}