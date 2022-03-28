package ua.nanit.airalarm.alarm

import android.app.*
import ua.nanit.airalarm.NOTIFICATION_ID_MAIN
import ua.nanit.airalarm.R
import ua.nanit.airalarm.alarm.notification.AlarmNotification
import ua.nanit.airalarm.alarm.notification.AllClearNotification
import ua.nanit.airalarm.util.Resources

class NotificationAlarm(private val service: Service) : Alarm {

    private val imgAlarmOn = Resources.getVectorBitmap(service, R.drawable.ic_alarm_on)
    private val imgAlarmOff = Resources.getVectorBitmap(service, R.drawable.ic_alarm_off)

    override fun alarm() {
        alarm(true)
    }

    override fun allClear() {
        allClear(true)
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