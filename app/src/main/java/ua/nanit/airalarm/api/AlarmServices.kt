/*
 * Copyright (C) 2022 Nanit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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