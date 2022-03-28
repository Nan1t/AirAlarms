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

package ua.nanit.airalarm.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ua.nanit.airalarm.R
import ua.nanit.airalarm.api.ApiClient
import ua.nanit.airalarm.region.Region
import ua.nanit.airalarm.region.Regions
import ua.nanit.airalarm.ui.region.RegionAdapter
import ua.nanit.airalarm.ui.region.RegionClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ua.nanit.airalarm.PREFS_KEY_REGION_ID
import ua.nanit.airalarm.PREFS_KEY_REGION_NAME
import ua.nanit.airalarm.util.Resources

class RegionsActivity : LocalizedActivity(R.layout.activity_regions),
    Callback<Regions>,
    RegionClickListener {

    private lateinit var progressBar: ProgressBar
    private lateinit var list: RecyclerView
    private lateinit var btnSelectState: Button
    private lateinit var regions: Regions

    private var isStatesList = true
    private var currentState: Region? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressBar = findViewById(R.id.regions_progressbar)
        list = findViewById(R.id.regions_list)
        btnSelectState = findViewById(R.id.btn_select_whole_state)
        list.layoutManager = LinearLayoutManager(applicationContext)

        btnSelectState.setOnClickListener(this::onClickSelectAll)

        requestRegions()
    }

    override fun onResponse(call: Call<Regions>, response: Response<Regions>) {
        if (response.isSuccessful) {
            val body = response.body()

            if (body != null) {
                regions = body
                displayStates()
                return
            }
        }

        showFailToast("Failure")
    }

    override fun onFailure(call: Call<Regions>, t: Throwable) {
        showFailToast(t.message ?: "")
        requestRegions()
    }

    override fun onClick(region: Region) {
        if (region.isState()) {
            displayDistricts(region)
        } else if(region.isDistrict()) {
            selectRegion(region)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            displayStates()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!isStatesList) {
            displayStates()
            return
        }

        super.onBackPressed()
    }

    private fun onClickSelectAll(view: View) {
        if (currentState != null) {
            AlertDialog.Builder(this)
                .setTitle(R.string.regions_dialog_title)
                .setMessage(R.string.regions_dialog_content)
                .setNegativeButton(R.string.action_no) { _, _ -> }
                .setPositiveButton(R.string.action_yes) { _, _ ->
                    selectRegion(currentState!!)
                }
                .create()
                .show()
        }
    }

    private fun selectRegion(region: Region) {
        val prefs = Resources.getSettings(this)

        prefs.edit()
            .putInt(PREFS_KEY_REGION_ID, region.id)
            .putString(PREFS_KEY_REGION_NAME, region.name)
            .apply()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        startActivity(intent)
        finish()
    }

    private fun displayStates() {
        currentState = null

        setTitle(R.string.regions_title_state)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        progressBar.visibility = View.GONE
        list.visibility = View.VISIBLE
        btnSelectState.visibility = View.GONE

        list.adapter = RegionAdapter(regions.getStates(), this)
        isStatesList = true
    }

    private fun displayDistricts(state: Region) {
        currentState = state

        val districts = regions.getDistricts(state)

        if (districts.isEmpty()) {
            selectRegion(state)
            return
        }

        setTitle(R.string.regions_title_district)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        progressBar.visibility = View.GONE
        list.visibility = View.VISIBLE
        btnSelectState.visibility = View.VISIBLE

        list.adapter = RegionAdapter(districts, this)
        isStatesList = false
    }

    private fun requestRegions() {
        ApiClient.services.getRegions()
            .enqueue(this)
    }

    private fun showFailToast(msg: String) {
        Toast.makeText(applicationContext, "Cannot load regions: $msg", Toast.LENGTH_SHORT)
            .show()
    }
}
