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

package ua.nanit.airalarm.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ua.nanit.airalarm.ACTION_ALARM
import ua.nanit.airalarm.AlarmView
import ua.nanit.airalarm.service.AlarmService

class AlarmReceiver(private val view: AlarmView) : BroadcastReceiver() {

    override fun onReceive(ctx: Context, intent: Intent) {
        if (intent.action == ACTION_ALARM) {
            val args = intent.extras!!
            val alarmed = args.getBoolean("alarmed")

            if (alarmed) {
                view.activateAlarm()
            } else {
                view.deactivateAlarm()
            }
        }
    }

}