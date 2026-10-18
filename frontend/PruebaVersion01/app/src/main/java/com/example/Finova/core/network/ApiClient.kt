package com.example.Finova.core.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val BASE_URL = "http://192.168.0.190:8080/"

    // 🛠️ PUENTE DINÁMICO: Variable global en memoria para inyectar el JWT Token
    var tokenDinamico: String? = null

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        // 🔒 INTERCEPTOR AUTOMÁTICO DE JWT
        .addInterceptor { chain ->
            val requestOriginal = chain.request()

            // Si el token dinámico tiene contenido, lo inyectamos en la cabecera Authorization
            val nuevaPeticion = if (!tokenDinamico.isNullOrBlank()) {
                requestOriginal.newBuilder()
                    .addHeader("Authorization", "Bearer $tokenDinamico")
                    .build()
            } else {
                requestOriginal
            }

            chain.proceed(nuevaPeticion)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
