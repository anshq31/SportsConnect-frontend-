package com.ansh.sportsapp.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class AuthResponseDto (
    @SerializedName("accessToken")val accessToken : String,
    @SerializedName("refreshToken")val refreshToken : String,
    @SerializedName("id")val id : Long,
    @SerializedName("username")val username : String,
    @SerializedName("email")val email : String
)

