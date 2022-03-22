package ua.nanit.airalarm.region

data class RegionStatus(
    val alarmed: Boolean,
    val alarms: List<Alarm>
) {
    override fun toString(): String {
        return "RegionStatus(alarmed=$alarmed, alarms=$alarms)"
    }
}
