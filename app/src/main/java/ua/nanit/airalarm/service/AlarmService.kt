package ua.nanit.airalarm.service

import android.app.*
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ua.nanit.airalarm.api.ApiClient
import ua.nanit.airalarm.region.RegionStatus
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class AlarmService : Service(), Callback<RegionStatus> {

    companion object {
        const val ACTION_ALARM = "ua.nanit.airalarm.ACTION_ALARM"
    }

    private val executor = Executors.newSingleThreadScheduledExecutor()

    private lateinit var prefs: SharedPreferences
    private lateinit var notificator: Notificator

    private var task: ScheduledFuture<*>? = null
    private var regionId = -1
    private var alarmed = false

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences("settings", MODE_PRIVATE)
        notificator = Notificator(this)
        Log.d("AirAlarm", "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        regionId = prefs.getInt("regionId", -1)
        alarmed = prefs.getBoolean("alarmed", false)

        if (task == null) {
            task = executor.scheduleWithFixedDelay(this::checkAlarms,
                0, 5, TimeUnit.SECONDS)
        }

        notificator.startForegroundNotification()

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

        if (!status.alarmed && !alarmed && status.hasAlarmedRegion(regionId)) {
            changeAlarmStatus(true)
        }
    }

    private fun changeAlarmStatus(alarmed: Boolean) {
        this.alarmed = alarmed

        prefs.edit()
            .putBoolean("alarmed", alarmed)
            .apply()

        notificator.displayActualNotification(alarmed)

        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(Intent(ACTION_ALARM)
                .putExtra("alarmed", alarmed))

        Log.i("AirAlarm", "CHANGED ALARM STATUS TO $alarmed")
    }
}