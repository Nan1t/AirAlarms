package ua.nanit.airalarm.alarm.notification

import android.app.Service
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import ua.nanit.airalarm.R

class AlarmNotification(
    service: Service,
    btnMute: Boolean,
    private val icon: Bitmap
    ) : MuteableNotification(service, btnMute) {

    override fun modify(builder: NotificationCompat.Builder) {
        super.modify(builder)

        builder.color = service.resources.getColor(R.color.danger)
        builder.setSmallIcon(R.drawable.ic_baseline_warning)
        builder.setLargeIcon(icon)
        builder.setContentTitle(service.getString(R.string.status_alarmed_title))
        builder.setContentText(service.getString(R.string.status_alarmed_subtitle))
    }

}