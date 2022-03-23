package ua.nanit.airalarm.ui

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ua.nanit.airalarm.AlarmView
import ua.nanit.airalarm.R
import ua.nanit.airalarm.receivers.AlarmReceiver
import ua.nanit.airalarm.service.AlarmService
import java.lang.Exception

class MainActivity : AppCompatActivity(R.layout.activity_main), AlarmView {

    private lateinit var rootView: View

    private lateinit var btnVolume: FloatingActionButton
    private lateinit var btnSettings: FloatingActionButton
    private lateinit var btnUnsubscribe: Button

    private lateinit var alarmImage: ImageView
    private lateinit var alarmText: AppCompatTextView
    private lateinit var regionName: TextView
    private lateinit var prefs: SharedPreferences
    private lateinit var receiver: AlarmReceiver

    private var currentRegion = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        prefs = getSharedPreferences("settings", MODE_PRIVATE)

        currentRegion = prefs.getInt("regionId", -1)

        if (currentRegion == -1) {
            openRegionsListActivity()
            return
        }

        receiver = AlarmReceiver(this)

        rootView = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        btnVolume = findViewById(R.id.main_btn_volume)
        btnSettings = findViewById(R.id.main_btn_settings)
        btnUnsubscribe = findViewById(R.id.btn_unsubscribe)
        alarmImage = findViewById(R.id.status_icon)
        alarmText = findViewById(R.id.status_text)
        regionName = findViewById(R.id.current_region)

        regionName.text = prefs.getString("regionName", "Region undefined")

        btnVolume.setOnClickListener(this::onClick)
        btnSettings.setOnClickListener(this::onClick)
        btnUnsubscribe.setOnClickListener(this::onClick)

        val alarmed = prefs.getBoolean("alarmed", false)

        if (alarmed) {
            activateAlarm()
        } else {
            deactivateAlarm()
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter(AlarmService.ACTION_ALARM))

        startService(Intent(this, AlarmService::class.java))
    }

    override fun activateAlarm() {
        rootView.setBackgroundResource(R.color.danger)
        alarmImage.setImageResource(R.drawable.ic_baseline_warning)
        alarmText.setText(R.string.status_alarmed)
    }

    override fun deactivateAlarm() {
        rootView.setBackgroundResource(R.color.success)
        alarmImage.setImageResource(R.drawable.ic_baseline_check)
        alarmText.setText(R.string.status_ok)
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
                prefs.edit()
                    .remove("alarmed")
                    .remove("regionId")
                    .remove("regionName")
                    .apply()

                openRegionsListActivity()
            }
        }
    }

    private fun openRegionsListActivity() {
        stopService(Intent(this, AlarmService::class.java))
        val intent = Intent(this, RegionsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun requestBackgroundPerms() {
        val manufacturer = android.os.Build.MANUFACTURER

        try {
            val intent = Intent()

            when {
                "xiaomi".equals(manufacturer, true) -> {
                    intent.component = ComponentName("com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity")
                }
                "oppo".equals(manufacturer, true) -> {
                    intent.component = ComponentName("com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity")
                }
                "vivo".equals(manufacturer, true) -> {
                    intent.component = ComponentName("com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")
                }
                "Letv".equals(manufacturer, true) -> {
                    intent.component = ComponentName("com.letv.android.letvsafe",
                        "com.letv.android.letvsafe.AutobootManageActivity")
                }
                "Honor".equals(manufacturer, true) -> {
                    intent.component = ComponentName("com.huawei.systemmanager",
                        "com.huawei.systemmanager.optimize.process.ProtectActivity")
                }
            }

            val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

            if (list.size > 0)
                startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}