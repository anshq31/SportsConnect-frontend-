package com.ansh.sportsapp.presentation.user_profile

import com.ansh.sportsapp.domain.model.UserProfile

data class UserProfileState (
    val profile : UserProfile? = null,
    val isLoading : Boolean = false,
    val error : String? = null,
)