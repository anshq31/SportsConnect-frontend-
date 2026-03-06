package com.ansh.sportsapp.domain.usecase.user

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.model.UserProfile
import com.ansh.sportsapp.domain.repository.UserRepository
import javax.inject.Inject

class GetMyProfileUseCase @Inject constructor(
    private val repository: UserRepository
){
    suspend operator fun invoke(): Resource<UserProfile>{
        return repository.getMyProfile()
    }
}