package com.ansh.sportsapp.presentation.create_gig

import com.ansh.sportsapp.data.remote.dto.nominatim.NominatimResultDto

sealed class CreateGigEvent {
    data class EnteredSport(val value: String) : CreateGigEvent()
    data class LocationQueryChanged(val query: String) : CreateGigEvent()
    data class SuggestionSelected(val result: NominatimResultDto) : CreateGigEvent()
    data class MarkerDragged(val lat: Double, val lng: Double) : CreateGigEvent()
    data class EnteredDate(val value: String) : CreateGigEvent()
    data class EnteredTime(val value: String) : CreateGigEvent()
    data class EnteredPlayers(val value: String) : CreateGigEvent()
    object Submit : CreateGigEvent()
}
