package com.ansh.sportsapp.presentation.my_gigs

import com.ansh.sportsapp.domain.model.GigRequest

data class MyGigsState (
    val requests: List<GigRequest> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)