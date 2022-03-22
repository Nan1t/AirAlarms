package me.nanit.ukrainealarms.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import me.nanit.ukrainealarms.R

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var rootView: View
    private lateinit var alarmImage: ImageView
    private lateinit var alarmText: TextView
    private lateinit var regionName: TextView
    private lateinit var btnUnsubscribe: Button
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        preferences = getSharedPreferences("settings", MODE_PRIVATE)

        val currentRegion = preferences.getInt("regionId", -1)

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

    private fun onUnsubscribeClick(view: View) {
        preferences.edit()
            .remove("regionId")
            .remove("regionName")
            .apply()

        openRegionsListActivity()
    }

    private fun activateAlarm() {
        rootView.setBackgroundResource(R.color.danger)
        alarmImage.setImageResource(R.drawable.ic_baseline_warning)
        alarmText.setText(R.string.status_alarmed)
    }

    private fun deactivateAlarm() {
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

}