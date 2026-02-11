package com.ansh.sportsapp.domain.repository

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.data.remote.dto.auth.AuthResponseDto

interface AuthRepository {
    suspend fun register(username : String,email : String, password : String): Resource<Boolean>
    suspend fun login(username: String, password: String): Resource<AuthResponseDto>
}