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