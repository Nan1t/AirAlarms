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

    override fun toString(): String {
        return "Region(id=$id, " +
                "name='$name', " +
                "stateId=$stateId, " +
                "districtId=$districtId, " +
                "regionType=$regionType)"
    }


}
