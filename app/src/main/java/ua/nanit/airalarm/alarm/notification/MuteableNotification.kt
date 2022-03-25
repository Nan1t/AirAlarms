package ua.nanit.airalarm.alarm.notification

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import androidx.core.app.NotificationCompat
import ua.nanit.airalarm.R
import ua.nanit.airalarm.service.AlarmService

abstract class MuteableNotification(
    service: Service,
    private val btnMute: Boolean
) : BaseNotification(service) {

    override fun modify(builder: NotificationCompat.Builder) {
        super.modify(builder)

        if (btnMute) {
            builder.addAction(
                R.drawable.ic_baseline_volume_off,
                service.getString(R.string.notification_btn_mute),
                getMuteIntent())
        }
    }

    private fun getMuteIntent(): PendingIntent {
        val intent = Intent(service, AlarmService::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra(AlarmService.CMD_STOP_SIGNAL, true)
        return PendingIntent.getService(service, 0, intent, 0)
    }
}