package ua.nanit.airalarm.region

data class RegionStatus(
    val alarmed: Boolean,
    val alarms: List<Alarm>
)
