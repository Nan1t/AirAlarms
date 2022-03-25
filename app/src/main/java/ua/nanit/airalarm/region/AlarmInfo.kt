package ua.nanit.airalarm.region

data class AlarmInfo(
    val type: RegionType,
    val regionId: Int,
    val districtId: Int?,
    val communityId: Int?,
) {
    override fun toString(): String {
        return "Alarm(type=$type, " +
                "regionId=$regionId, " +
                "districtId=$districtId, " +
                "communityId=$communityId)"
    }
}