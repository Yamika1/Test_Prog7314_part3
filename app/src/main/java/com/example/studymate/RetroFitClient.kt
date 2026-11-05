package com.example.studymate

import android.icu.util.TimeUnit
import okhttp3.OkHttpClient
import okio.Timeout
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetroFitClient {

    private const val BASE_URL = "https://studymateapi-two.onrender.com/"

    val apiClient: OkHttpClient = OkHttpClient.Builder().connectTimeout(35,
        java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(35, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(35,java.util.concurrent.TimeUnit.SECONDS)
        .build()

    val api : ApiService by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(apiClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

    }
}