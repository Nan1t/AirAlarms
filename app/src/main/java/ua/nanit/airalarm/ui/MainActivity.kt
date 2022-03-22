package ua.nanit.airalarm.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ua.nanit.airalarm.R

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var rootView: View

    private lateinit var btnVolume: FloatingActionButton
    private lateinit var btnSettings: FloatingActionButton
    private lateinit var btnUnsubscribe: Button

    private lateinit var alarmImage: ImageView
    private lateinit var alarmText: AppCompatTextView
    private lateinit var regionName: TextView
    private lateinit var preferences: SharedPreferences

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
        btnVolume = findViewById(R.id.main_btn_volume)
        btnSettings = findViewById(R.id.main_btn_settings)
        btnUnsubscribe = findViewById(R.id.btn_unsubscribe)
        alarmImage = findViewById(R.id.status_icon)
        alarmText = findViewById(R.id.status_text)
        regionName = findViewById(R.id.current_region)

        regionName.text = preferences.getString("regionName", "Region undefined")

        btnVolume.setOnClickListener(this::onClick)
        btnSettings.setOnClickListener(this::onClick)
        btnUnsubscribe.setOnClickListener(this::onClick)

        deactivateAlarm()
        startAlarmService()
    }

    private fun startAlarmService() {

    }

    private fun onClick(view: View) {
        when (view.id) {
            R.id.main_btn_volume -> {
                // TODO
            }
            R.id.main_btn_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.btn_unsubscribe -> {
                preferences.edit()
                    .remove("regionId")
                    .remove("regionName")
                    .apply()

                openRegionsListActivity()
            }
        }
    }

    private fun activateAlarm() {
        alarmed = true
        rootView.setBackgroundResource(R.color.danger)
        alarmImage.setImageResource(R.drawable.ic_baseline_warning)
        alarmText.setText(R.string.status_alarmed)
    }

    private fun deactivateAlarm() {
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

}