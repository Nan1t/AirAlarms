package ua.nanit.airalarm.alarm

import android.app.*
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.app.NotificationCompat
import ua.nanit.airalarm.NOTIFICATION_CHANNEL
import ua.nanit.airalarm.NOTIFICATION_ID_PUSH
import ua.nanit.airalarm.R

class NotificationAlarm(private val ctx: Context) : Alarm {

    private val notifyManager = ctx.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
    private val imgAlarmOn = BitmapFactory.decodeResource(ctx.resources, R.drawable.alarm_on)
    private val imgAlarmOff = BitmapFactory.decodeResource(ctx.resources, R.drawable.alarm_off)

    override fun alarm() {
        notifyManager.notify(NOTIFICATION_ID_PUSH, getNotifyAlarm())
    }

    override fun cancelAlarm() {
        notifyManager.notify(NOTIFICATION_ID_PUSH, getNotifyCancel())
    }

    private fun getNotifyAlarm(): Notification {
        val builder = NotificationCompat.Builder(ctx, NOTIFICATION_CHANNEL)

        builder.priority = NotificationCompat.PRIORITY_MAX
        builder.color = ctx.resources.getColor(R.color.danger)
        builder.setCategory(Notification.CATEGORY_ALARM)
        builder.setChannelId("Alarm")
        builder.setSmallIcon(R.drawable.ic_baseline_warning)
        builder.setLargeIcon(imgAlarmOn)
        builder.setContentTitle(ctx.getString(R.string.status_alarmed_title))
        builder.setContentText(ctx.getString(R.string.status_alarmed_subtitle))
        builder.setWhen(System.currentTimeMillis())
        builder.setLights(Color.RED, 400, 200)
        builder.setAutoCancel(true)

        return builder.build()
    }

    private fun getNotifyCancel(): Notification {
        val builder = NotificationCompat.Builder(ctx, NOTIFICATION_CHANNEL)

        builder.priority = NotificationCompat.PRIORITY_MAX
        builder.color = ctx.resources.getColor(R.color.success)
        builder.setCategory(Notification.CATEGORY_ALARM)
        builder.setChannelId(NotificationChannel.EDIT_IMPORTANCE)
        builder.setSmallIcon(R.drawable.ic_baseline_check)
        builder.setLargeIcon(imgAlarmOff)
        builder.setContentTitle(ctx.getString(R.string.alarm_cancelled))
        builder.setContentText(ctx.getString(R.string.status_ok_subtitle))
        builder.setWhen(System.currentTimeMillis())
        builder.setLights(Color.GREEN, 400, 200)
        builder.setAutoCancel(true)

        return builder.build()
    }

}