package com.ansh.sportsapp.data.remote.dto.nominatim

import com.google.gson.annotations.SerializedName

data class NominatimResultDto(
    val lat: String,
    val lon: String,
    @SerializedName("display_name") val displayName: String
)
