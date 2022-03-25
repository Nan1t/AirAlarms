package ua.nanit.airalarm.ui

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ua.nanit.airalarm.*
import ua.nanit.airalarm.receivers.AlarmReceiver
import ua.nanit.airalarm.service.AlarmService

class MainActivity : AppCompatActivity(R.layout.activity_main), AlarmView {

    private lateinit var rootView: View

    private lateinit var btnVolume: FloatingActionButton
    private lateinit var btnSettings: FloatingActionButton
    private lateinit var btnUnsubscribe: Button

    private lateinit var statusImage: ImageView
    private lateinit var statusTitle: TextView
    private lateinit var statusSubtitle: AppCompatTextView
    private lateinit var regionName: TextView
    private lateinit var prefs: SharedPreferences
    private lateinit var receiver: AlarmReceiver
    private lateinit var notifyManager: NotificationManager

    private var currentRegion = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        prefs = Resources.getSettings(this)

        currentRegion = prefs.getInt(PREFS_KEY_REGION_ID, -1)

        if (currentRegion == -1) {
            openRegionsListActivity()
            return
        }

        notifyManager = getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        receiver = AlarmReceiver(this)

        rootView = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        btnVolume = findViewById(R.id.main_btn_volume)
        btnSettings = findViewById(R.id.main_btn_settings)
        btnUnsubscribe = findViewById(R.id.btn_unsubscribe)
        statusImage = findViewById(R.id.status_icon)
        statusTitle = findViewById(R.id.status_title)
        statusSubtitle = findViewById(R.id.status_subtitle)
        regionName = findViewById(R.id.current_region)

        regionName.text = prefs.getString(PREFS_KEY_REGION_NAME, "Region undefined")

        btnVolume.setOnClickListener(this::onClick)
        btnSettings.setOnClickListener(this::onClick)
        btnUnsubscribe.setOnClickListener(this::onClick)

        val alarmed = prefs.getBoolean(PREFS_KEY_ALARMED, false)

        if (alarmed) {
            activateAlarm()
        } else {
            deactivateAlarm()
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter(ACTION_ALARM))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, AlarmService::class.java))
        } else {
            startService(Intent(this, AlarmService::class.java))
        }

//        if (isServiceKiller()) {
//
//        }
    }

    override fun onResume() {
        super.onResume()
        updateVolumeIcon()
    }

    override fun activateAlarm() {
        rootView.setBackgroundResource(R.color.danger)
        statusImage.setImageResource(R.drawable.ic_baseline_warning)
        statusTitle.setText(R.string.status_alarmed_title)
        statusSubtitle.setText(R.string.status_alarmed_subtitle)
    }

    override fun deactivateAlarm() {
        rootView.setBackgroundResource(R.color.success)
        statusImage.setImageResource(R.drawable.ic_baseline_check)
        statusTitle.setText(R.string.status_ok_title)
        statusSubtitle.setText(R.string.status_ok_subtitle)
    }

    private fun onClick(view: View) {
        when (view.id) {
            btnVolume.id -> switchVolume()
            btnSettings.id -> startActivity(Intent(this, SettingsActivity::class.java))
            btnUnsubscribe.id -> openRegionsListActivity()
        }
    }

    private fun switchVolume() {
        val volume = prefs.getInt(PREFS_KEY_VOLUME, VOLUME_DEFAULT)

        if (volume == 0) {
            val saved = prefs.getInt(PREFS_KEY_VOLUME_SAVED, VOLUME_DEFAULT)

            prefs.edit()
                .putInt(PREFS_KEY_VOLUME, saved)
                .apply()
        } else {
            prefs.edit()
                .putInt(PREFS_KEY_VOLUME_SAVED, volume)
                .putInt(PREFS_KEY_VOLUME, 0)
                .apply()
        }

        updateVolumeIcon()
    }

    private fun updateVolumeIcon() {
        val volume = prefs.getInt(PREFS_KEY_VOLUME, VOLUME_DEFAULT)

        if (volume == 0) {
            btnVolume.setImageResource(R.drawable.ic_baseline_volume_off)
        } else {
            btnVolume.setImageResource(R.drawable.ic_baseline_volume_up)
        }
    }

    private fun openRegionsListActivity() {
        prefs.edit()
            .remove(PREFS_KEY_ALARMED)
            .remove(PREFS_KEY_REGION_ID)
            .remove(PREFS_KEY_REGION_NAME)
            .apply()

        stopService(Intent(this, AlarmService::class.java))
        val intent = Intent(this, RegionsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

//    private fun isServiceKiller(): Boolean {
//        return when (Build.MANUFACTURER.lowercase()) {
//            "xiaomi" -> true
//            "oppo" -> true
//            "vivo" -> true
//            "letv" -> true
//            "honor" -> true
//            "meizu" -> true
//            else -> false
//        }
//    }

}