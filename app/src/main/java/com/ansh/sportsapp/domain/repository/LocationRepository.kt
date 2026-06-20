package com.ansh.sportsapp.domain.repository

import com.ansh.sportsapp.domain.model.UserLocation

interface LocationRepository {
    suspend fun getCurrentLocation(): UserLocation?
    fun hasLocationPermission(): Boolean
}
