package ua.nanit.airalarm.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ua.nanit.airalarm.R
import ua.nanit.airalarm.api.ApiClient
import ua.nanit.airalarm.region.RegionStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(R.layout.activity_main), Callback<RegionStatus> {

    private lateinit var rootView: View
    private lateinit var alarmImage: ImageView
    private lateinit var alarmText: TextView
    private lateinit var regionName: TextView
    private lateinit var btnUnsubscribe: Button
    private lateinit var preferences: SharedPreferences

    private val executor = Executors.newSingleThreadScheduledExecutor()
    private var task: ScheduledFuture<*>? = null

    private var alarmed = false
    private var currentRegion = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        preferences = getSharedPreferences("settings", MODE_PRIVATE)

        currentRegion = preferences.getInt("regionId", -1)

        if (currentRegion == -1) {
            openRegionsListActivity()
            return
        }

        rootView = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        alarmImage = findViewById(R.id.status_icon)
        alarmText = findViewById(R.id.status_text)
        regionName = findViewById(R.id.current_region)
        btnUnsubscribe = findViewById(R.id.btn_unsubscribe)

        regionName.text = preferences.getString("regionName", "Region undefined")

        btnUnsubscribe.setOnClickListener(this::onUnsubscribeClick)

        deactivateAlarm()
    }

    override fun onResume() {
        super.onResume()
        task = executor.scheduleAtFixedRate(this::checkAlarm, 0, 5, TimeUnit.SECONDS)
    }

    override fun onPause() {
        super.onPause()
        task?.cancel(true)
        executor?.shutdown()
    }

    override fun onResponse(call: Call<RegionStatus>, response: Response<RegionStatus>) {
        val status = response.body()

        if (status != null) {
            if (!status.alarmed && !alarmed) {
                activateAlarm()
                return
            }

            if (status.alarmed && alarmed) {
                deactivateAlarm()
            }
        }
    }

    override fun onFailure(call: Call<RegionStatus>, t: Throwable) {
        Log.e("error", "CANNOT GET REGION STATUS")
    }

    private fun onUnsubscribeClick(view: View) {
        preferences.edit()
            .remove("regionId")
            .remove("regionName")
            .apply()

        openRegionsListActivity()
    }

    private fun activateAlarm() {
        Log.i("info", "ALARM ACTIVATE")
        alarmed = true
        rootView.setBackgroundResource(R.color.danger)
        alarmImage.setImageResource(R.drawable.ic_baseline_warning)
        alarmText.setText(R.string.status_alarmed)
    }

    private fun deactivateAlarm() {
        Log.i("info", "ALARM DEACTIVATE")
        alarmed = false
        rootView.setBackgroundResource(R.color.success)
        alarmImage.setImageResource(R.drawable.ic_baseline_check)
        alarmText.setText(R.string.status_ok)
    }

    private fun openRegionsListActivity() {
        val intent = Intent(this, RegionsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun checkAlarm() {
        ApiClient.services.checkStatus(currentRegion)
            .enqueue(this)
    }

}