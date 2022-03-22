package ua.nanit.airalarm.api

import ua.nanit.airalarm.AlarmApp
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val client: Retrofit
    val services: AlarmServices

    init {
        client = Retrofit.Builder()
            .baseUrl(AlarmApp.BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        services = client.create(AlarmServices::class.java)
    }

}