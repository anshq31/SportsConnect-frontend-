package com.ansh.sportsapp.domain.usecase.gig

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.model.GigRequest
import com.ansh.sportsapp.domain.repository.GigRepository
import javax.inject.Inject

class GetMyRequestUseCase @Inject constructor(
    private val repository: GigRepository
){
    suspend operator fun invoke(): Resource<List<GigRequest>> {
        return repository.getMyGigRequests()
    }
}