package ua.nanit.airalarm.region

data class Alarm(
    val type: RegionType,
    val regionId: Int,
    val districtId: Int?,
    val communityId: Int?,
)