package ua.nanit.airalarm.alarm

import android.app.*
import android.graphics.BitmapFactory
import ua.nanit.airalarm.NOTIFICATION_ID_MAIN
import ua.nanit.airalarm.R
import ua.nanit.airalarm.alarm.notification.AlarmNotification
import ua.nanit.airalarm.alarm.notification.AllClearNotification

class NotificationAlarm(private val service: Service) : Alarm {

    private val imgAlarmOn = BitmapFactory.decodeResource(service.resources, R.drawable.ic_alarm_on)
    private val imgAlarmOff = BitmapFactory.decodeResource(service.resources, R.drawable.ic_alarm_off)

    override fun alarm() {
        val notification = AlarmNotification(service, true, imgAlarmOn)
        service.startForeground(NOTIFICATION_ID_MAIN, notification.build())
    }

    override fun allClear() {
        val notification = AllClearNotification(service, true, imgAlarmOff)
        service.startForeground(NOTIFICATION_ID_MAIN, notification.build())
    }

    override fun stop() {
        // Ignore for notifications
    }

    fun alarm(withAction: Boolean) {
        val notification = AlarmNotification(service, withAction, imgAlarmOn)
        service.startForeground(NOTIFICATION_ID_MAIN, notification.build())
    }

    fun allClear(withAction: Boolean) {
        val notification = AllClearNotification(service, withAction, imgAlarmOff)
        service.startForeground(NOTIFICATION_ID_MAIN, notification.build())
    }

}