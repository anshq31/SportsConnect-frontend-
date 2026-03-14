package com.ansh.sportsapp.domain.usecase.review

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.data.remote.dto.user.ReviewRequestDto
import com.ansh.sportsapp.domain.repository.ReviewRepository
import javax.inject.Inject

class SubmitReviewUseCase @Inject constructor(
    private val repository: ReviewRepository
) {
    suspend operator fun invoke(request : ReviewRequestDto): Resource<Unit>{
        return repository.submitReview(request)
    }
}