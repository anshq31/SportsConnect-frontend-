package com.ansh.sportsapp.data.repository

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.data.remote.SportsApi
import com.ansh.sportsapp.data.remote.dto.user.ReviewDto
import com.ansh.sportsapp.data.remote.dto.user.ReviewRequestDto
import com.ansh.sportsapp.domain.model.Review
import com.ansh.sportsapp.domain.repository.ReviewRepository
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val api : SportsApi
) : ReviewRepository{
    override suspend fun submitReview(request: ReviewRequestDto): Resource<Unit> {
        return try {
            api.submitReview(request)
            Resource.Success(Unit)
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "An error occurred while submitting review")
        }
    }

    override suspend fun getReviewsForUser(
        userId: Long,
        page: Int,
        size: Int
    ): Resource<List<Review>> {
        return try {
            val response = api.getReviewsForUser(userId, page, size)
            Resource.Success(response.content.map {it.toDomain()})
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "An error occurred while fetching reviews")
        }
    }

    private fun ReviewDto.toDomain(): Review{
        return Review(
            id = id,
            reviewerUsername = reviewerUsername,
            gigId = gigId,
            rating = rating,
            comment = comment ?: ""
        )
    }
}