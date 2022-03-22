package me.nanit.ukrainealarms.region

data class Region(
    val id: Int,
    val name: String,
    val stateId: Int,
    var districtId: Int? = null,
    val regionType: RegionType,
) {

    fun isState(): Boolean {
        return regionType == RegionType.STATE
    }

    fun isDistrict(): Boolean {
        return regionType == RegionType.DISTRICT
    }

    fun isDistrictOf(region: Region): Boolean {
        return isDistrict() && this.stateId == region.stateId
    }

}
