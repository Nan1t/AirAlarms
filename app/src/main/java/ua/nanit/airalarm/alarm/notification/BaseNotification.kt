package ua.nanit.airalarm.alarm.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import androidx.core.app.NotificationCompat
import ua.nanit.airalarm.NOTIFICATION_CHANNEL
import ua.nanit.airalarm.service.AlarmService
import ua.nanit.airalarm.ui.MainActivity

abstract class BaseNotification(val service: Service) {

    private val builder: NotificationCompat.Builder =
        NotificationCompat.Builder(service, NOTIFICATION_CHANNEL)

    protected open fun modify(builder: NotificationCompat.Builder) {
        builder.priority = NotificationCompat.PRIORITY_MAX
        builder.setCategory(Notification.CATEGORY_ALARM)
        builder.setChannelId(NotificationChannel.EDIT_IMPORTANCE)
        builder.setContentIntent(getActivityIntent())
        builder.setAutoCancel(true)
    }

    fun build(): Notification {
        modify(builder)
        return builder.build()
    }

    private fun getActivityIntent(): PendingIntent {
        return PendingIntent.getActivity(service, 0,
            Intent(service, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
    }

}