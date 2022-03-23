package ua.nanit.airalarm.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ua.nanit.airalarm.service.AlarmService


class BootReceiver : BroadcastReceiver() {

    override fun onReceive(ctx: Context?, intent: Intent?) {
        if (ctx == null) return
        if (intent == null) return

        ctx.startService(Intent(ctx, AlarmService::class.java))
        Log.i("AirAlarm", "STARTED SERVICE ON BOOT COMPLETED")
    }

}