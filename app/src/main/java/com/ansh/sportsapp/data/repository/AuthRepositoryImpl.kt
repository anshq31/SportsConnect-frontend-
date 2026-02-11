package com.ansh.sportsapp.data.repository


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
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
        Log.d("AUTH_REPO", "login() entered")
        println("DEBUG_AUTH: Login started for user: $username")

        return try {
            val request = LoginRequestDto(username = username, password = password)
            println("DEBUG_AUTH: Sending request to API...")
            val result = api.login(request)
            println("DEBUG_AUTH: Response received: ${result.accessToken.take(10)}...")
            authPreferences.saveAuthData(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
                userId = result.id.toString(),
                username = result.username
            )

            Resource.Success(result)
        }catch (e : HttpException){
            println("DEBUG_AUTH: HttpException code ${e.code()}")
            Resource.Error(e.message() ?: "Login Failed")
        }catch (e : IOException){
            println("DEBUG_AUTH: IOException - ${e.message}")
            Resource.Error("Couldn't reach server. Check your internet connection.")
        }catch (e: Exception) {
            // JSON Parsing error or other crash
            println("DEBUG_AUTH: CRASH! ${e.message}")
            e.printStackTrace()
            Resource.Error("App Error: ${e.localizedMessage}")
        }
    }
}