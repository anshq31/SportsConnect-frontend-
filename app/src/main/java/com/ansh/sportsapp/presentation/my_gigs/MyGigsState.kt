package com.ansh.sportsapp.presentation.my_gigs

import com.ansh.sportsapp.domain.model.Gig
import com.ansh.sportsapp.domain.model.GigRequest

data class MyGigsState (
    val joinedGigs : List<Gig> = emptyList(),
    val createdGig : List<Gig> = emptyList(),
    val requests: List<GigRequest> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)