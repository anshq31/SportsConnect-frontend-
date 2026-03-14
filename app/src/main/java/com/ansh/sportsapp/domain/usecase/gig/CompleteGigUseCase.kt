package com.ansh.sportsapp.domain.usecase.gig

import com.ansh.sportsapp.domain.model.Gig
import com.ansh.sportsapp.domain.repository.GigRepository
import javax.inject.Inject

class CompleteGigUseCase @Inject constructor(
    private val repository: GigRepository
) {
    suspend operator fun invoke(gigId : Long) : Gig {
        return repository.completeGig(gigId)
    }
}