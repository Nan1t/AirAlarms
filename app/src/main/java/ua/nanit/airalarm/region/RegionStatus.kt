package ua.nanit.airalarm.region

data class RegionStatus(
    val alarmed: Boolean,
    val alarms: List<AlarmInfo>?
) {

    fun hasAlarmedRegion(regionId: Int): Boolean {
        if (alarms != null) {
            for (alarm in alarms) {
                if (alarm.regionId == regionId) {
                    return true
                }
            }
        }
        return false
    }

    override fun toString(): String {
        return "RegionStatus(alarmed=$alarmed, alarms=$alarms)"
    }
}
