package com.ansh.sportsapp.domain.usecase.user

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.model.UserProfile
import com.ansh.sportsapp.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase@Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: Long): Resource<UserProfile> = repository.getUserProfile(userId)
}