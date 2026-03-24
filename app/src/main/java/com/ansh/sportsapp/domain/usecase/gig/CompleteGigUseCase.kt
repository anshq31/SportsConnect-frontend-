package com.ansh.sportsapp.domain.usecase.gig

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.model.Gig
import com.ansh.sportsapp.domain.repository.GigRepository
import javax.inject.Inject

class CompleteGigUseCase @Inject constructor(
    private val repository: GigRepository
) {
    suspend operator fun invoke(gigId : Long) : Resource<Gig> {
        return repository.completeGig(gigId)
    }
}