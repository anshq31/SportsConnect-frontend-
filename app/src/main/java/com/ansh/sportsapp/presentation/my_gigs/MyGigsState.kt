package com.ansh.sportsapp.presentation.my_gigs

import com.ansh.sportsapp.domain.model.Gig
import com.ansh.sportsapp.domain.model.GigRequest

data class MyGigsState (
    val joinedGigs : List<Gig> = emptyList(),
    val createdGig : List<Gig> = emptyList(),
    val isCreatedGigsLoading : Boolean = false,
    val isJoinedGigsLoading : Boolean = false,
    val error: String? = null
){
    val isLoading : Boolean
        get() = isCreatedGigsLoading || isJoinedGigsLoading
}