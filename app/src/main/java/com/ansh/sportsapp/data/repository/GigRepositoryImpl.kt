package com.ansh.sportsapp.data.repository

import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.data.remote.SportsApi
import com.ansh.sportsapp.data.remote.dto.gig.CreateGigRequestDto
import com.ansh.sportsapp.data.remote.dto.gig.GigDto
import com.ansh.sportsapp.data.remote.dto.gig.GigRequestDto
import com.ansh.sportsapp.domain.model.Gig
import com.ansh.sportsapp.domain.model.GigRequest
import com.ansh.sportsapp.domain.model.GigStatus
import com.ansh.sportsapp.domain.model.Participant
import com.ansh.sportsapp.domain.repository.GigRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val ERR_BLOCKED = "403"

class GigRepositoryImpl @Inject constructor(
    private val api: SportsApi,
) : GigRepository{
    override suspend fun getActiveGigs(
        sport: String?,
        lat: Double?,
        lng: Double?,
        radiusKm: Int?
    ): Resource<List<Gig>> {
        return withContext(Dispatchers.IO){
            try {
                val response = api.getActiveGigs(
                    sport = sport?.ifBlank { null },
                    lat = lat,
                    lng = lng,
                    radiusKm = if (lat != null) radiusKm else null
                )
                val gigs = response.content.map { it.toDomain() }
                Resource.Success(gigs)
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.Error("Failed to load gigs: ${e.localizedMessage}")
            }
        }
    }

    override suspend fun getGigParticipatedIn(): Resource<List<Gig>> {
        return withContext(Dispatchers.IO){
            try {
                val response = api.getGigParticipatedIn()
                val gigs = response.content.map { it.toDomain() }
                Resource.Success(gigs)
            } catch (e : Exception){
                e.printStackTrace()
                Resource.Error("Failed to load gigs: ${e.localizedMessage}")
            }
        }
    }

    override suspend fun getGigByGigMaster(): Resource<List<Gig>> {
         return withContext(Dispatchers.IO){
             try {
                val response = api.getGigByGigMaster()

                val gigs = response.content.map { it.toDomain() }

                Resource.Success(gigs)
            }catch (e : Exception){
                e.printStackTrace()
                Resource.Error("Failed to load gigs: ${e.localizedMessage}")
            }
        }
    }

    override suspend fun createGig(
        sport: String,
        location: String,
        latitude: Double?,
        longitude: Double?,
        dateTime: String,
        playersNeeded: Int
    ): Resource<Boolean> {
        return withContext(Dispatchers.IO){
            try {
                val request = CreateGigRequestDto(
                    sport = sport,
                    location = location,
                    latitude = latitude,
                    longitude = longitude,
                    dateTime = dateTime,
                    playersNeeded = playersNeeded
                )
                api.createGig(request)
                Resource.Success(true)
            } catch (e: HttpException) {
                Resource.Error(e.message() ?: "Failed to create gig")
            } catch (e: IOException) {
                Resource.Error("Network error. Check connection.")
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.Error("Unknown error: ${e.localizedMessage}")
            }
        }
    }

    override suspend fun getGigById(gigId: Long): Resource<Gig> {
        return withContext(Dispatchers.IO) {
            try {
                val dto = api.getGigById(gigId)
                Resource.Success(dto.toDomain())
            } catch (e: HttpException) {
                if (e.code() == 403) Resource.Error(ERR_BLOCKED)
                else Resource.Error("Failed to load gig details")
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.Error("Failed to load gig details")
            }
        }
    }

    override suspend fun requestJoin(gigId: Long): Resource<Boolean> {
        return try {
            val response = api.requestJoin(gigId)
            when {
                response.isSuccessful -> Resource.Success(true)
                response.code() == 403 -> Resource.Error(ERR_BLOCKED)
                else -> Resource.Error("Failed to join: ${response.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error("Error: ${e.localizedMessage}")
        }
    }

    override suspend fun getMyGigRequests(): Resource<List<GigRequest>> {
        return withContext(Dispatchers.IO){
            try {
                val response = api.getMyGigRequests()
                val requests = response.content.map { it.toDomain() }
                Resource.Success(requests)
            }catch (e: Exception){
                e.printStackTrace()
                Resource.Error("Failed to load requests: ${e.localizedMessage}")
            }
        }
    }

    override suspend fun acceptRequest(requestId: Long): Resource<Boolean> {
        return try {
            val response = api.acceptRequest(requestId)
            if (response.isSuccessful) Resource.Success(true)
            else Resource.Error("Failed to accept: ${response.code()}")
        }catch (e : Exception){
            e.printStackTrace()
            Resource.Error("Error: ${e.localizedMessage}")
        }
    }

    override suspend fun rejectRequest(requestId: Long): Resource<Boolean> {
        return try {
            val response = api.rejectRequest(requestId)
            if (response.isSuccessful) Resource.Success(true)
            else Resource.Error("Failed to reject: ${response.code()}")
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error("Error: ${e.localizedMessage}")
        }
    }

    override suspend fun completeGig(gigId: Long): Resource<Gig> {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(api.completeGig(gigId).toDomain())
            } catch (e: Exception) {
                Resource.Error("Failed to complete gig")
            }
        }
    }

    private fun GigDto.toDomain(): Gig {
        return Gig(
            id = id,
            sport = sport,
            location = location,
            dateTime = dateTime,
            playersNeeded = playersNeeded,
            gigMasterUsername = gigMasterUsername ?: "Unknown",
            isOwner = isOwner,
            isParticipant = isParticipant,
            requestStatus = requestStatus,
            status = runCatching {
                GigStatus.valueOf(status)
            }.getOrDefault(GigStatus.ACTIVE),
            acceptedParticipants = acceptedParticipant.map {
                Participant(id = it.id, username = it.username)
            },
            latitude = latitude,
            longitude = longitude,
            gigMasterId = gigMasterId
        )
    }

    private fun GigRequestDto.toDomain(): GigRequest{
        return GigRequest(
            requestId = requestId,
            requesterId = requesterId,
            requesterUsername = requesterUsername
        )
    }
}