package com.ansh.sportsapp.domain.usecase.gig

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.model.Gig
import com.ansh.sportsapp.domain.repository.GigRepository
import javax.inject.Inject

class GetActiveGigUseCase @Inject constructor(
    private val repository: GigRepository
){
    suspend operator fun invoke(sport : String? = null , location : String? = null): Resource<List<Gig>> {
        return repository.getActiveGigs(sport,location)
    }
}