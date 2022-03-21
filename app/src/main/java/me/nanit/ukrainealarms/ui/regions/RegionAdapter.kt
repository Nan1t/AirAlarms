package me.nanit.ukrainealarms.ui.regions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.nanit.ukrainealarms.R
import me.nanit.ukrainealarms.region.Region

class RegionAdapter(
    private val regions: List<Region>,
    private val clickListener: RegionClickListener
) : RecyclerView.Adapter<RegionAdapter.RegionHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_region, parent)

        return RegionHolder(view)
    }

    override fun onBindViewHolder(holder: RegionHolder, position: Int) {
        val region = regions[position]
        holder.bind(region)
    }

    override fun getItemCount(): Int = regions.size

    inner class RegionHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val nameField: TextView = view.findViewById(R.id.region_name)
        private val selectBtn: Button = view.findViewById(R.id.region_btn_select)

        fun bind(region: Region) {
            nameField.text = region.name

            selectBtn.setOnClickListener {
                clickListener.onClick(region)
            }
        }

    }

}