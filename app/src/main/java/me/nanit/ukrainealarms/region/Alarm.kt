package me.nanit.ukrainealarms.region

data class Alarm(
    val type: RegionType,
    val regionId: Int,
    val districtId: Int?,
    val communityId: Int?,
)