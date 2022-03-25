package ua.nanit.airalarm.alarm

import android.app.*
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import ua.nanit.airalarm.NOTIFICATION_CHANNEL
import ua.nanit.airalarm.NOTIFICATION_ID_MAIN
import ua.nanit.airalarm.R
import ua.nanit.airalarm.ui.MainActivity

class NotificationAlarm(private val service: Service) : Alarm {

    private val imgAlarmOn = BitmapFactory.decodeResource(service.resources, R.drawable.alarm_on)
    private val imgAlarmOff = BitmapFactory.decodeResource(service.resources, R.drawable.alarm_off)

    override fun alarm() {
        service.startForeground(NOTIFICATION_ID_MAIN, getNotifyAlarm())
    }

    override fun allClear() {
        service.startForeground(NOTIFICATION_ID_MAIN, getNotifyCancel())
    }

    private fun getNotifyAlarm(): Notification {
        val builder = NotificationCompat.Builder(service, NOTIFICATION_CHANNEL)

        builder.priority = NotificationCompat.PRIORITY_MAX
        builder.color = service.resources.getColor(R.color.danger)
        builder.setCategory(Notification.CATEGORY_ALARM)
        builder.setChannelId(NotificationChannel.EDIT_IMPORTANCE)
        builder.setSmallIcon(R.drawable.ic_baseline_warning)
        builder.setLargeIcon(imgAlarmOn)
        builder.setContentTitle(service.getString(R.string.status_alarmed_title))
        builder.setContentText(service.getString(R.string.status_alarmed_subtitle))
        builder.setContentIntent(getIntent())

        return builder.build()
    }

    private fun getNotifyCancel(): Notification {
        val builder = NotificationCompat.Builder(service, NOTIFICATION_CHANNEL)

        builder.priority = NotificationCompat.PRIORITY_MAX
        builder.color = service.resources.getColor(R.color.success)
        builder.setCategory(Notification.CATEGORY_ALARM)
        builder.setChannelId(NotificationChannel.EDIT_IMPORTANCE)
        builder.setSmallIcon(R.drawable.ic_baseline_check)
        builder.setLargeIcon(imgAlarmOff)
        builder.setContentTitle(service.getString(R.string.status_ok_title))
        builder.setContentText(service.getString(R.string.status_ok_subtitle))
        builder.setContentIntent(getIntent())

        return builder.build()
    }

    private fun getIntent(): PendingIntent {
        return PendingIntent.getActivity(service,
            0, Intent(service, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
    }

}