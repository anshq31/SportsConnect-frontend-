package com.ansh.sportsapp.data.remote.dto.auth

data class RegisterRequestDto(
    val username : String,
    val email : String,
    val password : String
)