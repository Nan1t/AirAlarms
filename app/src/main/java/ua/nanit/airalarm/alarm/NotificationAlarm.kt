/*
 * Copyright (C) 2022 Nanit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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