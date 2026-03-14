package com.ansh.sportsapp.domain.usecase.review

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.model.Review
import com.ansh.sportsapp.domain.repository.ReviewRepository
import javax.inject.Inject

class GetReviewUseCase @Inject constructor(
    private val repository : ReviewRepository
) {
    suspend operator fun invoke(userId: Long,page: Int = 0, size: Int = 10): Resource<List<Review>>{
        return repository.getReviewsForUser(userId, page, size)
    }
}