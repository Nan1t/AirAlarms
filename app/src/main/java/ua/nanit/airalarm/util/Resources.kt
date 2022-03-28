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
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.preference.PreferenceManager

object Resources {

    fun getSettings(ctx: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    fun getResUri(ctx: Context, resId: Int): Uri {
        return Uri.parse(getResUriStr(ctx, resId))
    }

    fun getVectorBitmap(ctx: Context, resId: Int): Bitmap {
        return AppCompatResources.getDrawable(ctx, resId)?.toBitmap()!!
    }

    private fun getResUriStr(ctx: Context, resId: Int): String {
        return "android.resource://${ctx.packageName}/$resId"
    }

}