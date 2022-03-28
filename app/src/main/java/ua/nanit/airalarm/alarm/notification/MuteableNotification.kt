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