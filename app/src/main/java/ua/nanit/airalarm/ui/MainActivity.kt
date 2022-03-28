package ua.nanit.airalarm.ui

import android.app.AlertDialog
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ua.nanit.airalarm.*
import ua.nanit.airalarm.receivers.AlarmReceiver
import ua.nanit.airalarm.service.AlarmService
import ua.nanit.airalarm.util.Resources

class MainActivity : LocalizedActivity(R.layout.activity_main), AlarmView {

    private lateinit var rootView: View

    private lateinit var prefs: SharedPreferences
    private lateinit var notifyManager: NotificationManager
    private lateinit var connManager: ConnectivityManager

    private lateinit var btnShutdown: FloatingActionButton
    private lateinit var btnSettings: FloatingActionButton
    private lateinit var btnUnsubscribe: Button
    private lateinit var statusImage: ImageView
    private lateinit var statusTitle: TextView
    private lateinit var statusSubtitle: AppCompatTextView
    private lateinit var regionName: TextView

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
        connManager = getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager

        checkNetworkState()

        rootView = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        btnShutdown = findViewById(R.id.main_btn_shutdown)
        btnSettings = findViewById(R.id.main_btn_settings)
        btnUnsubscribe = findViewById(R.id.btn_unsubscribe)
        statusImage = findViewById(R.id.status_icon)
        statusTitle = findViewById(R.id.status_title)
        statusSubtitle = findViewById(R.id.status_subtitle)
        regionName = findViewById(R.id.current_region)

        regionName.text = prefs.getString(PREFS_KEY_REGION_NAME, "Region undefined")

        btnShutdown.setOnClickListener(this::onClick)
        btnSettings.setOnClickListener(this::onClick)
        btnUnsubscribe.setOnClickListener(this::onClick)

        val alarmed = prefs.getBoolean(PREFS_KEY_ALARMED, false)

        if (alarmed) {
            activateAlarm()
        } else {
            deactivateAlarm()
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(AlarmReceiver(this), IntentFilter(ACTION_ALARM))

        val serviceIntent = Intent(this, AlarmService::class.java)
            .putExtra(AlarmService.CMD_STOP_SIGNAL, true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        if (isServiceKiller()) {
            AlertDialog.Builder(this)
                .setTitle(R.string.main_dialog_title)
                .setMessage(R.string.main_dialog_content)
                .setPositiveButton(R.string.action_ok) { _, _ ->
                    prefs.edit()
                        .putBoolean(PREFS_KEY_NOTICED_SERVICE_KILLER, true)
                        .apply()
                }
                .create()
                .show()
        }
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
            btnShutdown.id -> {
                btnShutdown.isEnabled = false
                stopService(Intent(this, AlarmService::class.java))
                Toast.makeText(this, R.string.main_stopped, Toast.LENGTH_LONG)
                    .show()
            }
            btnSettings.id -> startActivity(Intent(this, SettingsActivity::class.java))
            btnUnsubscribe.id -> openRegionsListActivity()
        }
    }

    private fun openRegionsListActivity() {
        prefs.edit()
            .remove(PREFS_KEY_ALARMED)
            .remove(PREFS_KEY_REGION_ID)
            .remove(PREFS_KEY_REGION_NAME)
            .apply()

        startService(Intent(this, AlarmService::class.java)
            .putExtra(AlarmService.CMD_STOP_SIGNAL, true))

        stopService(Intent(this, AlarmService::class.java))

        startActivity(Intent(this, RegionsActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

        finish()
    }

    private fun checkNetworkState() {
        val hasConnection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connManager.activeNetwork != null
        } else {
            connManager.activeNetworkInfo != null
        }

        if (!hasConnection) {
            Toast.makeText(this, R.string.main_no_network, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun isServiceKiller(): Boolean {
        if (prefs.getBoolean(PREFS_KEY_NOTICED_SERVICE_KILLER, false))
            return false

        return when (Build.MANUFACTURER.lowercase()) {
            "xiaomi" -> true
            "oppo" -> true
            "vivo" -> true
            "letv" -> true
            "honor" -> true
            "meizu" -> true
            else -> {
                prefs.edit()
                    .putBoolean(PREFS_KEY_NOTICED_SERVICE_KILLER, true)
                    .apply()

                return false
            }
        }
    }

}