package com.ansh.sportsapp.presentation.home

import com.ansh.sportsapp.domain.model.Gig

data class HomeState(
    val gigs: List<Gig> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val sportQuery: String = "",
    val locationQuery: String = "",
)