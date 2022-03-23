package ua.nanit.airalarm.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ua.nanit.airalarm.AlarmView
import ua.nanit.airalarm.service.AlarmService

class AlarmReceiver(private val view: AlarmView) : BroadcastReceiver() {

    override fun onReceive(ctx: Context, intent: Intent) {
        if (intent.action == AlarmService.ACTION_ALARM) {
            val args = intent.extras!!
            val alarmed = args.getBoolean("alarmed")

            if (alarmed) {
                view.activateAlarm()
            } else {
                view.deactivateAlarm()
            }
        }
    }

}