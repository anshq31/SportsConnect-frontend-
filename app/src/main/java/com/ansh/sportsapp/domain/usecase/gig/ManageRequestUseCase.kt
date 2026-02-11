package com.ansh.sportsapp.domain.usecase.gig

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.repository.GigRepository
import javax.inject.Inject

class ManageRequestUseCase @Inject constructor(
    private val repository: GigRepository
){
    suspend fun accept(requestId: Long): Resource<Boolean> {
        return repository.acceptRequest(requestId)
    }

    suspend fun reject(requestId: Long): Resource<Boolean> {
        return repository.rejectRequest(requestId)
    }
}