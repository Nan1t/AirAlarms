package ua.nanit.airalarm.alarm

import android.app.*
import android.graphics.BitmapFactory
import ua.nanit.airalarm.NOTIFICATION_ID_MAIN
import ua.nanit.airalarm.R
import ua.nanit.airalarm.alarm.notification.AlarmNotification
import ua.nanit.airalarm.alarm.notification.AllClearNotification

class NotificationAlarm(private val service: Service) : Alarm {

    private val imgAlarmOn = BitmapFactory.decodeResource(service.resources, R.drawable.alarm_on)
    private val imgAlarmOff = BitmapFactory.decodeResource(service.resources, R.drawable.alarm_off)

    private val notifyAlarm = AlarmNotification(service, false, imgAlarmOn)
        .build()
    private val notifyAlarmMute = AlarmNotification(service, true, imgAlarmOn)
        .build()
    private val notifyAllClear = AllClearNotification(service, false, imgAlarmOff)
        .build()
    private val notifyAllClearMute = AllClearNotification(service, true, imgAlarmOff)
        .build()

    override fun alarm() {
        service.startForeground(NOTIFICATION_ID_MAIN, notifyAlarmMute)
    }

    override fun allClear() {
        service.startForeground(NOTIFICATION_ID_MAIN, notifyAllClearMute)
    }

    override fun stop() {
        // Ignore for notifications
    }

    fun alarm(withAction: Boolean) {
        val notification = if (withAction) notifyAlarmMute else notifyAlarm
        service.startForeground(NOTIFICATION_ID_MAIN, notification)
    }

    fun allClear(withAction: Boolean) {
        val notification = if (withAction) notifyAllClearMute else notifyAllClear
        service.startForeground(NOTIFICATION_ID_MAIN, notification)
    }

}