package ua.nanit.airalarm.service

import android.app.*
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ua.nanit.airalarm.R
import ua.nanit.airalarm.api.ApiClient
import ua.nanit.airalarm.region.RegionStatus
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class AlarmService : Service(), Callback<RegionStatus> {

    companion object {
        const val ACTION_ALARM = "ua.nanit.airalarm.ACTION_ALARM"
    }

    private lateinit var alarmManager: AlarmManager
    private lateinit var prefs: SharedPreferences

    private val executor = Executors.newSingleThreadScheduledExecutor()
    private var task: ScheduledFuture<*>? = null
    private var regionId = -1
    private var alarmed = false

    override fun onCreate() {
        super.onCreate()
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        prefs = getSharedPreferences("settings", MODE_PRIVATE)
        Log.d("AirAlarm", "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        regionId = prefs.getInt("regionId", -1)
        alarmed = prefs.getBoolean("alarmed", false)

        if (task == null) {
            task = executor.scheduleWithFixedDelay(this::checkAlarms,
                1, 5, TimeUnit.SECONDS)
        }

        Log.d("AirAlarm", "Service started")
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("AirAlarm", "Service stopping")
        super.onDestroy()
        task?.cancel(true)
        executor.shutdown()
        Log.d("AirAlarm", "Service stopped")
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onResponse(call: Call<RegionStatus>, response: Response<RegionStatus>) {
        val status = response.body()

        if (status != null) {
            Log.d("AirAlarm", "Get status: $status")
            checkStatus(status)
        } else {
            Log.d("AirAlarm", "Status body is null!")
        }
    }

    override fun onFailure(call: Call<RegionStatus>, t: Throwable) {
        Log.i("AirAlarm", "Error while retrieving status: ${t.message}")
    }

    private fun checkAlarms() {
        if (regionId != -1) {
            ApiClient.services.checkStatus(regionId)
                .enqueue(this)
        } else {
            Log.d("AirAlarm", "Region not selected")
        }
    }

    private fun checkStatus(status: RegionStatus) {
        if (status.alarmed && alarmed) {
            changeAlarmStatus(false)
            return
        }

        if (!status.alarmed && !alarmed) {
            for (alarm in status.alarms) {
                if (alarm.regionId == regionId) {
                    changeAlarmStatus(true)
                    return
                }
            }
        }
    }

    private fun changeAlarmStatus(alarmed: Boolean) {
        this.alarmed = alarmed

        prefs.edit()
            .putBoolean("alarmed", alarmed)
            .apply()

        val intent = Intent(ACTION_ALARM)
            .putExtra("alarmed", alarmed)

        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(intent)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, "alarms")
            .setSmallIcon(R.drawable.ic_baseline_warning)
            .setContentTitle("AIR ALARM!")
            .setContentText("Go to the closest shelter")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()

        manager.notify(1, notification)

        Log.i("AirAlarm", "CHANGED ALARM STATUS TO $alarmed")
    }
}