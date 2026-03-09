package com.ansh.sportsapp.domain.repository

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.model.UserProfile

interface UserRepository {
    suspend fun getMyProfile(): Resource<UserProfile>
    suspend fun updateMyProfile(experience : String, skillIds : Set<Long>): Resource<UserProfile>

    suspend fun getUserProfile(userId: Long): Resource<UserProfile>
}