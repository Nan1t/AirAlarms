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
import ua.nanit.airalarm.*
import ua.nanit.airalarm.alarm.*
import ua.nanit.airalarm.api.ApiClient
import ua.nanit.airalarm.region.RegionStatus
import ua.nanit.airalarm.ui.MainActivity
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class AlarmService : Service(), Callback<RegionStatus> {

    private val executor = Executors.newSingleThreadScheduledExecutor()

    private lateinit var prefs: SharedPreferences
    private lateinit var alarm: Alarm

    private var task: ScheduledFuture<*>? = null
    private var regionId = -1
    private var alarmed = false

    override fun onCreate() {
        super.onCreate()
        prefs = Resources.getSettings(this)
        alarm = MultipleAlarm(
            NotificationAlarm(this),
            VibrationAlarm(this),
            SoundAlarm(this)
        )
        Log.d("AirAlarm", "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        regionId = prefs.getInt("regionId", -1)
        alarmed = prefs.getBoolean("alarmed", false)

        if (task == null) {
            task = executor.scheduleWithFixedDelay(this::checkAlarms,
                0, 5, TimeUnit.SECONDS)
        }

        val pIntent = PendingIntent.getActivity(this,
            0, Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pIntent)

        notification.setSmallIcon(R.drawable.ic_megaphone)
        notification.setContentTitle(this.getString(R.string.service_notification_title))
        notification.setContentText(this.getString(R.string.service_notification_subtitle))

        startForeground(NOTIFICATION_ID_MAIN, notification.build())

        Log.d("AirAlarm", "Service started")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        task?.cancel(true)
        executor.shutdown()
        Log.d("AirAlarm", "Service stopped")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onResponse(call: Call<RegionStatus>, response: Response<RegionStatus>) {
        val status = response.body()

        if (status != null) {
            if (!status.alarmed && !alarmed && status.hasAlarmedRegion(regionId)) {
                alarm()
            }

            if (status.alarmed && alarmed && !status.hasAlarmedRegion(regionId)) {
                cancelAlarm()
                return
            }
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
            Log.d("AirAlarm", "Region is not selected")
        }
    }

    private fun alarm() {
        this.alarmed = true
        saveToPrefs()
        broadcastStatus()
        alarm.alarm()
    }

    private fun cancelAlarm() {
        this.alarmed = false
        saveToPrefs()
        broadcastStatus()
        alarm.cancelAlarm()
    }

    private fun broadcastStatus() {
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(Intent(ACTION_ALARM)
                .putExtra("alarmed", alarmed))
    }

    private fun saveToPrefs() {
        prefs.edit()
            .putBoolean("alarmed", alarmed)
            .apply()

        Log.i("AirAlarm", "Alarm status changed to $alarmed")
    }


}