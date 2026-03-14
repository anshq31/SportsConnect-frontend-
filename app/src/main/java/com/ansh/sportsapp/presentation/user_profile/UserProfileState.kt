package com.ansh.sportsapp.presentation.user_profile

import com.ansh.sportsapp.domain.model.Review
import com.ansh.sportsapp.domain.model.UserProfile

data class UserProfileState (
    val profile : UserProfile? = null,
    val reviews : List<Review> = emptyList(),
    val isReviewLoading : Boolean = false,
    val isLoading : Boolean = false,
    val error : String? = null,
    val reviewsError : String? = null
)