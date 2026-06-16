package com.ansh.sportsapp.data.repository


import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.data.local.AuthPreferences
import com.ansh.sportsapp.data.remote.SportsApi
import com.ansh.sportsapp.data.remote.dto.auth.AuthResponseDto
import com.ansh.sportsapp.data.remote.dto.auth.LoginRequestDto
import com.ansh.sportsapp.data.remote.dto.auth.RegisterRequestDto
import com.ansh.sportsapp.domain.repository.AuthRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api : SportsApi,
    private val authPreferences: AuthPreferences
) : AuthRepository {
    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): Resource<Boolean> {
        return try {
            val request = RegisterRequestDto(username = username, email = email, password = password)
            val response = api.register(request)
            if (response.isSuccessful){
                Resource.Success(true)
            }else{
                Resource.Error("Registration failed: ${response.code()}")
            }
        }catch (e : HttpException){
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }catch (e : IOException){
            Resource.Error("Couldn't reach server. Check your internet connection.")
        } catch (e: Exception) {
            e.printStackTrace() // Log the real crash
            Resource.Error("Unknown error: ${e.localizedMessage}")
        }
    }

    override suspend fun login(
        username: String,
        password: String
    ): Resource<AuthResponseDto> {
        return try {
            val request = LoginRequestDto(username = username, password = password)
            val result = api.login(request)
            authPreferences.saveAuthData(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
                userId = result.id.toString(),
                username = result.username
            )
            Resource.Success(result)
        } catch (e : HttpException){
            Resource.Error(e.message() ?: "Login Failed")
        } catch (e : IOException){
            Resource.Error("Couldn't reach server. Check your internet connection.")
        } catch (e: Exception) {
            Resource.Error("App Error: ${e.localizedMessage}")
        }
    }

    override suspend fun deleteAccount(): Resource<Unit> {
        return try {
            val response = api.deleteAccount()
            if (response.isSuccessful) {
                authPreferences.clearAuthData()
                Resource.Success(Unit)
            } else {
                Resource.Error("Failed to delete account: ${response.code()}")
            }
        } catch (e: HttpException) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            Resource.Error("Couldn't reach server. Check your internet connection.")
        } catch (e: Exception) {
            Resource.Error("Unknown error: ${e.localizedMessage}")
        }
    }
}