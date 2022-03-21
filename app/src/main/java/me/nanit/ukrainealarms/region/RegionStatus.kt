package me.nanit.ukrainealarms.region

data class RegionStatus(
    val alarmed: Boolean,
    val alarms: List<Alarm>
)
