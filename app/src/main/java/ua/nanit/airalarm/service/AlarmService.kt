/*
 * Copyright (C) 2022 Nanit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
import ua.nanit.airalarm.*
import ua.nanit.airalarm.alarm.*
import ua.nanit.airalarm.api.ApiClient
import ua.nanit.airalarm.region.RegionStatus
import ua.nanit.airalarm.util.LocaleUtil
import ua.nanit.airalarm.util.Resources
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class AlarmService : Service(), Callback<RegionStatus> {

    companion object {
        const val CMD_STOP_SIGNAL = "stop_signal"
    }

    private val executor = Executors.newSingleThreadScheduledExecutor()

    private lateinit var prefs: SharedPreferences
    private lateinit var notificationAlarm: NotificationAlarm
    private lateinit var alarm: Alarm

    private var task: ScheduledFuture<*>? = null
    private var regionId = -1
    private var alarmed = false

    override fun onCreate() {
        super.onCreate()
        LocaleUtil.updateLocale(this)
        prefs = Resources.getSettings(this)
        notificationAlarm = NotificationAlarm(this)
        alarm = MultipleAlarm(
            notificationAlarm,
            VibrationAlarm(this),
            SoundAlarm(this)
        )
        Log.d("AirAlarm", "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        regionId = prefs.getInt(PREFS_KEY_REGION_ID, -1)
        alarmed = prefs.getBoolean(PREFS_KEY_ALARMED, false)

        val stopSignal = intent?.extras?.get(CMD_STOP_SIGNAL)

        if (stopSignal != null) {
            alarm.stop()
        }

        if (alarmed && regionId != -1) {
            notificationAlarm.alarm(stopSignal == null)
        } else {
            notificationAlarm.allClear(stopSignal == null)
        }

        if (task == null) {
            task = executor.scheduleWithFixedDelay(this::checkForAlarm,
                0, 5, TimeUnit.SECONDS)
        }

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
                performAlarm()
            }

            if (status.alarmed && alarmed && !status.hasAlarmedRegion(regionId)) {
                performAllClear()
                return
            }
        } else {
            Log.d("AirAlarm", "Status body is null!")
        }
    }

    override fun onFailure(call: Call<RegionStatus>, t: Throwable) {
        Log.i("AirAlarm", "Error while retrieving status: ${t.message}")
    }

    private fun checkForAlarm() {
        if (regionId != -1) {
            ApiClient.services.checkStatus(regionId)
                .enqueue(this)
        } else {
            Log.d("AirAlarm", "Region is not selected")
        }
    }

    private fun performAlarm() {
        this.alarmed = true
        saveToPrefs()
        broadcastStatus()
        alarm.alarm()
    }

    private fun performAllClear() {
        this.alarmed = false
        saveToPrefs()
        broadcastStatus()
        alarm.allClear()
    }

    private fun broadcastStatus() {
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(Intent(ACTION_ALARM)
                .putExtra("alarmed", alarmed))
    }

    private fun saveToPrefs() {
        prefs.edit()
            .putBoolean(PREFS_KEY_ALARMED, alarmed)
            .apply()

        Log.i("AirAlarm", "Alarm status changed to $alarmed")
    }


}