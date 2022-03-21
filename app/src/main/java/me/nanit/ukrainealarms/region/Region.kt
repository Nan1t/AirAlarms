package me.nanit.ukrainealarms.region

data class Region(
    val id: Int,
    val name: String,
    val stateId: Int,
    var districtId: Int? = null,
    val regionType: RegionType,
)
