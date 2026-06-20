package com.ansh.sportsapp.domain.usecase.gig

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.repository.GigRepository
import javax.inject.Inject

class CreateGigUseCase @Inject constructor(
    private val repository: GigRepository
) {
    suspend operator fun invoke(
        sport: String,
        location: String,
        latitude: Double?,
        longitude: Double?,
        date: String,
        time: String,
        playersNeeded: String
    ): Resource<Boolean> {
        if (sport.isBlank() || location.isBlank() || date.isBlank() || time.isBlank() || playersNeeded.isBlank()) {
            return Resource.Error("All fields are required")
        }

        val players = playersNeeded.toIntOrNull()
        if (players == null || players <= 0) {
            return Resource.Error("Players needed must be a valid number")
        }

        val isoDateTime = "${date}T${time}:00"

        return repository.createGig(sport, location, latitude, longitude, isoDateTime, players)
    }
}