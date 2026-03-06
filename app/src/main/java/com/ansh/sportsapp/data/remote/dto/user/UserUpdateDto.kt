package com.ansh.sportsapp.data.remote.dto.user

data class UserUpdateDto(
    val experience : String,
    val skillIds : Set<Long>
)
