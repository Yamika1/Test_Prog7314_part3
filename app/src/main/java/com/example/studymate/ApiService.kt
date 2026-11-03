package com.example.studymate

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class HealthResponse(
    val status: String,
    val protocol: String,
    val firebase: String
)

data class LoginRequest(
    val email: String,
    val  password: String
)

data class LoginResponse(
    val message: String,
    val data: LoginData
)

data class LoginData(
    val id: String,
    val firstName: String,
    val lastName: String,
    val username: String?,
    val email: String,
    val role: String,
    val token: String,
    val refreshToken: String,
    val expiresIn: String
)

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
val message: String,
var token: String
)

data class LoginSSORequest(
    val idToken: String?
)



interface ApiService {

    @GET("/health")
   suspend fun checkHealth(): Response<HealthResponse>


    @POST("./api/auth/login")
    fun login(@Body request: LoginRequest) : Call<LoginResponse>


    @POST("./api/auth/register")
     fun register(@Body request: RegisterRequest) : Call<RegisterResponse>

    @POST("./api/auth/login/sso")
    fun loginSSO(@Body request: LoginSSORequest) : Call<LoginResponse>
}