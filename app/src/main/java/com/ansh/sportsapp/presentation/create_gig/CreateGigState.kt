package com.ansh.sportsapp.presentation.create_gig

import com.ansh.sportsapp.data.remote.dto.nominatim.NominatimResultDto

data class CreateGigState(
    val sport: String = "",
    val locationQuery: String = "",
    val locationDisplay: String = "",
    val locationSuggestions: List<NominatimResultDto> = emptyList(),
    val selectedLat: Double? = null,
    val selectedLng: Double? = null,
    val isSearchingLocation: Boolean = false,
    val date: String = "",
    val time: String = "",
    val players: String = "",
    val isLoading: Boolean = false
)
