package ua.nanit.airalarm.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_API_URL = "https://air-save.ops.ajax.systems/"

    private val client: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val services: AlarmServices =
        client.create(AlarmServices::class.java)

}