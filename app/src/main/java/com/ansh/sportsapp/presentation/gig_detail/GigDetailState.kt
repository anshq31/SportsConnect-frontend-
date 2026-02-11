package com.ansh.sportsapp.presentation.gig_detail

import com.ansh.sportsapp.domain.model.Gig

data class GigDetailState (
    val gig: Gig? = null,
    val isLoading: Boolean = false,
    val isJoinLoading: Boolean = false,
    val error: String? = null
)