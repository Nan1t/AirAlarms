package ua.nanit.airalarm.api

import ua.nanit.airalarm.region.RegionStatus
import ua.nanit.airalarm.region.Regions
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AlarmServices {

    @GET("/api/mobile/regions")
    fun getRegions(): Call<Regions>

    @GET("/api/mobile/status")
    fun checkStatus(@Query("regionId") regionId: Int): Call<RegionStatus>

}