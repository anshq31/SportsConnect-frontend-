package com.ansh.sportsapp.presentation.home

import com.ansh.sportsapp.domain.model.Gig
import com.ansh.sportsapp.domain.model.UserLocation

data class HomeState(
    val gigs: List<Gig> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val sportQuery: String = "",
    val userLocation: UserLocation? = null,
    val hasLocationPermission: Boolean = false,
    val nearMeActive: Boolean = false,
    val radiusKm: Int = 15,
)
