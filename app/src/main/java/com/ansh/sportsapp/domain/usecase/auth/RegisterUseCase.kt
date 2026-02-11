package com.ansh.sportsapp.domain.usecase.auth

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(username : String, email : String , password : String) : Resource<Boolean>{
        if (username.isBlank()|| email.isBlank()||password.isBlank()){
            return Resource.Error("All field are required")
        }
        return repository.register(username,email,password)
    }
}