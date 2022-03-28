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

package ua.nanit.airalarm.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import ua.nanit.airalarm.PREFS_KEY_LANG
import java.util.*

object LocaleUtil {

    fun updateLocale(ctx: Context) {
        val prefs = Resources.getSettings(ctx)
        val savedLocale = prefs.getString(PREFS_KEY_LANG, null)

        if (savedLocale != null) {
            val conf = Configuration()
            val locale = Locale(savedLocale)

            Locale.setDefault(locale)
            conf.setLocale(locale)

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
                ctx.createConfigurationContext(conf)
            } else {
                ctx.resources.updateConfiguration(conf, ctx.resources.displayMetrics)
            }
        }
    }

}