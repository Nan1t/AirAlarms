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