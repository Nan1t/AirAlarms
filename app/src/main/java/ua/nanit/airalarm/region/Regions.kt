package ua.nanit.airalarm.region

import java.util.*

data class Regions(val regions: List<Region>) {

    fun getStates(): List<Region> {
        val states = LinkedList<Region>()

        for (region in regions) {
            if (region.isState()) {
                states.add(region)
            }
        }

        return states
    }

    fun getDistricts(state: Region): List<Region> {
        if (state.regionType != RegionType.STATE)
            return emptyList()

        val districts = LinkedList<Region>()

        for (region in regions) {
            if (region.isDistrictOf(state)) {
                districts.add(region)
            }
        }

        return districts
    }

    override fun toString(): String {
        return "Regions(regions=$regions)"
    }


}
