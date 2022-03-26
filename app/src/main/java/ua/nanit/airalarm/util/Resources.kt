package ua.nanit.airalarm.util

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.preference.PreferenceManager

object Resources {

    fun getSettings(ctx: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    fun getResUri(ctx: Context, resId: Int): Uri {
        return Uri.parse(getResUriStr(ctx, resId))
    }

    fun getResUriStr(ctx: Context, resId: Int): String {
        return "android.resource://${ctx.packageName}/$resId"
    }

}