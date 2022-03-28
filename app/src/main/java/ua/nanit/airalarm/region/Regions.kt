/*
 * Copyright (C) 2022 Nanit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
