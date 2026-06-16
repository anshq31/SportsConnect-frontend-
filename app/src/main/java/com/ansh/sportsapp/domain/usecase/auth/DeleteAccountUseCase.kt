package com.ansh.sportsapp.domain.usecase.auth

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.repository.AuthRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Resource<Unit> = repository.deleteAccount()
}
