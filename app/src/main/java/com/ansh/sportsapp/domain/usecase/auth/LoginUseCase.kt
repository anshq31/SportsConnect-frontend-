package com.ansh.sportsapp.domain.usecase.auth

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.data.remote.dto.auth.AuthResponseDto
import com.ansh.sportsapp.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
){
    suspend operator fun invoke(username : String, password : String): Resource<AuthResponseDto> {
        if (username.isBlank() || password.isBlank()){
            return Resource.Error("Username or password cannot be empty")
        }
        return repository.login(username,password);
    }
}