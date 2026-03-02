package com.ansh.sportsapp.data.remote

import com.ansh.sportsapp.data.remote.dto.auth.AuthResponseDto
import com.ansh.sportsapp.data.remote.dto.auth.LoginRequestDto
import com.ansh.sportsapp.data.remote.dto.auth.RefreshRequestDto
import com.ansh.sportsapp.data.remote.dto.auth.RegisterRequestDto
import com.ansh.sportsapp.data.remote.dto.chat.ChatMessageDto
import com.ansh.sportsapp.data.remote.dto.common.PageResponseDto
import com.ansh.sportsapp.data.remote.dto.gig.CreateGigRequestDto
import com.ansh.sportsapp.data.remote.dto.gig.GigDto
import com.ansh.sportsapp.data.remote.dto.gig.GigRequestDto
import com.ansh.sportsapp.domain.model.Gig
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface  SportsApi {
    @POST("api/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequestDto): Response<ResponseBody>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequestDto): AuthResponseDto

    @GET("api/gigs/active")
    suspend fun getActiveGigs(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sport") sport: String? = null,
        @Query("location") location: String? = null
    ) : PageResponseDto<GigDto>

    @GET("api/gigs/joined")
    suspend fun getGigParticipatedIn(
        @Query("page")page: Int = 0,
        @Query("size") size: Int = 10
    ) : PageResponseDto<GigDto>

    @GET("api/gigs/created")
    suspend fun getGigByGigMaster(
        @Query("page")page: Int = 0,
        @Query("size")size: Int = 10
    ) : PageResponseDto<GigDto>

    @POST("api/gigs")
    suspend fun createGig(@Body request: CreateGigRequestDto) : GigDto

    @GET("api/gigs/{gigId}")
    suspend fun getGigById(@Path("gigId") gigId: Long): GigDto

    @POST("api/gigs/{gigId}/request-join")
    suspend fun requestJoin(@Path("gigId") gigId: Long): Response<ResponseBody>

    @GET("api/gigs/my-gig/requests")
    suspend fun getMyGigRequests(
        @Query("page")page: Int=0,
        @Query("size")size: Int = 10
    ): PageResponseDto<GigRequestDto>

    @POST("api/gigs/my-gig/requests/{requestId}/accept")
    suspend fun acceptRequest(@Path("requestId") requestId: Long): Response<ResponseBody>

    @POST("api/gigs/my-gig/requests/{requestId}/reject")
    suspend fun rejectRequest(@Path("requestId") requestId: Long): Response<ResponseBody>

    @GET("api/chat/{groupId}/history")
    suspend fun getChatHistory(
        @Path("groupId")groupId: Long ,
        @Query("page")page: Int = 0,
        @Query("size")size: Int = 20
    ): PageResponseDto<ChatMessageDto>
}