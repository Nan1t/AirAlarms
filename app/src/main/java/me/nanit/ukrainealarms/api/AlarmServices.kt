package me.nanit.ukrainealarms.api

import android.graphics.Region
import me.nanit.ukrainealarms.region.RegionStatus
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface AlarmServices {

    @GET("/api/mobile/regions")
    fun getRegions(): Call<List<Region>>

    @GET("/api/mobile/status?regionId={regionId}")
    fun checkStatus(@Path("regionId") regionId: Int): Call<RegionStatus>

}