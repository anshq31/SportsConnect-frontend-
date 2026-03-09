package com.ansh.sportsapp.presentation.my_profile

import com.ansh.sportsapp.domain.model.UserProfile

data class ProfileState(
    val profile : UserProfile? = null,
    val isLoading : Boolean = false,
    val error : String? = null,
)