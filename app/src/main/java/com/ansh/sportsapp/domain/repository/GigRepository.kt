package com.ansh.sportsapp.domain.repository

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.model.Gig
import com.ansh.sportsapp.domain.model.GigRequest

interface GigRepository {
    suspend fun getActiveGigs(): Resource<List<Gig>>

    suspend fun getGigParticipatedIn(): Resource<List<Gig>>

    suspend fun getGigByGigMaster(): Resource<List<Gig>>

    suspend fun createGig(sport: String, location : String, dateTime: String, playersNeeded: Int): Resource<Boolean>

    suspend fun getGigById(gigId: Long): Resource<Gig>
    suspend fun requestJoin(gigId: Long): Resource<Boolean>

    suspend fun getMyGigRequests(): Resource<List<GigRequest>>
    suspend fun acceptRequest(requestId: Long): Resource<Boolean>
    suspend fun rejectRequest(requestId: Long): Resource<Boolean>

}