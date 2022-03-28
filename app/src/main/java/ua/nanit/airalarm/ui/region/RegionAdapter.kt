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

package ua.nanit.airalarm.ui.region

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ua.nanit.airalarm.R
import ua.nanit.airalarm.region.Region

class RegionAdapter(
    private val regions: List<Region>,
    private val clickListener: RegionClickListener
) : RecyclerView.Adapter<RegionAdapter.RegionHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_region, parent, false)

        return RegionHolder(view)
    }

    override fun onBindViewHolder(holder: RegionHolder, position: Int) {
        val region = regions[position]
        holder.bind(region)
    }

    override fun getItemCount(): Int = regions.size

    inner class RegionHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val nameField: TextView = view.findViewById(R.id.region_name)

        fun bind(region: Region) {
            nameField.text = region.name

            view.setOnClickListener {
                clickListener.onClick(region)
            }
        }

    }

}