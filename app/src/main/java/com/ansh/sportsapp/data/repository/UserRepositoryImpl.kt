package com.ansh.sportsapp.data.repository

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.data.remote.SportsApi
import com.ansh.sportsapp.data.remote.dto.user.ReviewDto
import com.ansh.sportsapp.data.remote.dto.user.UserProfileDto
import com.ansh.sportsapp.data.remote.dto.user.UserUpdateDto
import com.ansh.sportsapp.domain.model.Review
import com.ansh.sportsapp.domain.model.UserProfile
import com.ansh.sportsapp.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api : SportsApi
) : UserRepository{
    override suspend fun getMyProfile(): Resource<UserProfile> {
        return try {
            val dto = api.getMyProfile()
            Resource.Success(dto.toDomain())
        }catch (e: Exception){
            Resource.Error(e.localizedMessage ?: "An error occurred while fetching profile")
        }
    }

    override suspend fun updateMyProfile(
        experience: String,
        skillIds: Set<Long>
    ): Resource<UserProfile> {
        return try {
            val request = UserUpdateDto(experience = experience, skillIds = skillIds)

            val dto = api.updateMyProfile(request = request)
            Resource.Success(dto.toDomain())
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "An error occurred while updating profile")
        }
    }

    override suspend fun getUserProfile(userId: Long): Resource<UserProfile> {
        return try {
            val dto = api.getUserProfile(userId)
            Resource.Success(dto.toDomain())
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "An error occurred while fetching user profile")
        }
    }

    private fun UserProfileDto.toDomain(): UserProfile{
        return UserProfile(
            id = id,
            username = username,
            experience = experience?:"",
            overallRating = overallRating ?: 0.0,
            skills = skill?.toList()?: emptyList(),
            reviews = reviewsReceived?.map { it.toDomain() }?: emptyList(),
        )
    }

    private fun ReviewDto.toDomain(): Review{
        return Review(
            id = id,
            gigId = gigId,
            reviewerUsername = reviewerUsername,
            rating = rating,
            comment = comment ?: "",
        )
    }
}