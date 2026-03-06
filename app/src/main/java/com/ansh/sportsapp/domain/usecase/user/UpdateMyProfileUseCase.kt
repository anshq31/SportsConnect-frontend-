package com.ansh.sportsapp.domain.usecase.user

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.model.UserProfile
import com.ansh.sportsapp.domain.repository.UserRepository
import javax.inject.Inject

class UpdateMyProfileUseCase @Inject constructor(
    private val repository: UserRepository
){
    suspend operator fun invoke(experience: String, skillIds : Set<Long>): Resource<UserProfile>{
        return repository.updateMyProfile(experience, skillIds)
    }
}