package me.nanit.ukrainealarms.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.nanit.ukrainealarms.R
import me.nanit.ukrainealarms.api.ApiClient
import me.nanit.ukrainealarms.region.Region
import me.nanit.ukrainealarms.region.Regions
import me.nanit.ukrainealarms.ui.regions.RegionAdapter
import me.nanit.ukrainealarms.ui.regions.RegionClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegionsActivity : AppCompatActivity(R.layout.activity_regions),
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

        ApiClient.services.getRegions()
            .enqueue(this)
    }

    override fun onResponse(call: Call<Regions>, response: Response<Regions>) {
        if (response.isSuccessful) {
            regions = response.body()!!
            displayStates()
            return
        }

        showFailToast("failure")
    }

    override fun onFailure(call: Call<Regions>, t: Throwable) {
        showFailToast(t.message ?: "")
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
            selectRegion(currentState!!)
        }
    }

    private fun selectRegion(region: Region) {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)

        prefs.edit()
            .putInt("regionId", region.id)
            .putString("regionName", region.name)
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

    private fun showFailToast(msg: String) {
        Toast.makeText(applicationContext, "Cannot load regions: $msg", Toast.LENGTH_SHORT)
            .show()
    }
}
